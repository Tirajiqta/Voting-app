package com.tu.votingapp.analytics.voting;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class FeatureAggregator {
    private static final ObjectMapper mapper = new ObjectMapper();
    public static final double ANOMALY_THRESHOLD_MULTIPLIER = 2.0;
    public static final double TREND_THRESHOLD = 0.05; // 5% change

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Raw vote counts per election -> candidateId or partyId -> count
     */
    private final Map<Long, Map<Long, Integer>> counts = new ConcurrentHashMap<>();

    /**
     * Historic snapshots for trend and anomaly detection.
     */
    @Getter
    private final Map<Long, List<Snapshot>> history = new ConcurrentHashMap<>();

    /**
     * Ingest a vote event JSON, update counts and record a snapshot.
     */
    public void aggregateEvent(String messageJson) {
        try {
            VoteEvent event = mapper.readValue(messageJson, VoteEvent.class);
            Long electionId = event.getElectionId();
            Long key = event.getCandidateId() != null ? event.getCandidateId() : event.getPartyId();
            logger.fine(() -> String.format("Processing vote event for election %d, key %d", electionId, key));

            counts.computeIfAbsent(electionId, k -> new ConcurrentHashMap<>());
            Map<Long, Integer> electionCounts = counts.get(electionId);
            int newCount = electionCounts.merge(key, 1, Integer::sum);
            logger.fine(() -> String.format("Updated count for key %d: %d", key, newCount));

            // Record snapshot
            Map<Long, Integer> snapshotMap = new HashMap<>(electionCounts);
            Snapshot snap = new Snapshot(System.currentTimeMillis(), snapshotMap);
            history.computeIfAbsent(electionId, k -> new ArrayList<>()).add(snap);
            logger.fine(() -> String.format("Snapshot added for election %d; total snapshots: %d",
                    electionId, history.get(electionId).size()));

        } catch (Exception e) {
            // Log JSON parsing or processing errors without leaking raw payload
            logger.log(Level.WARNING, "Failed to aggregate vote event", e);
        }
    }

    /**
     * Builds a feature vector: [ totalVotes, count1, count2, ... ]
     */
    public INDArray getFeatures(Long electionId) {
        Map<Long, Integer> electionCounts = counts.getOrDefault(electionId, Collections.emptyMap());
        int numCandidates = electionCounts.size();
        int total = electionCounts.values().stream().mapToInt(i -> i).sum();
        double[] arr = new double[numCandidates + 1];
        arr[0] = total;
        int i = 1;
        for (Integer count : electionCounts.values()) {
            arr[i++] = count;
        }
        INDArray features = Nd4j.create(arr).reshape(1, arr.length);
        logger.fine(() -> String.format("Features generated for election %d: total=%d, dimensions=%d", electionId, total, arr.length));
        return features;
    }

    /**
     * Simple JSON holder for vote events.
     */
    private static class VoteEvent {
        @Getter private Long electionId;
        @Getter private Long candidateId;
        @Getter private Long partyId;
        private long timestamp;
    }

    /**
     * Snapshot of counts at a point in time.
     */
    @Getter
    public static class Snapshot {
        private final long timestamp;
        private final Map<Long, Integer> counts;
        public Snapshot(long timestamp, Map<Long, Integer> counts) {
            this.timestamp = timestamp;
            this.counts = counts;
        }
    }
}
