package com.tu.votingapp.analytics.voting;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FeatureAggregator {
    private static final ObjectMapper mapper = new ObjectMapper();
    public static final double ANOMALY_THRESHOLD_MULTIPLIER = 2.0;
    public static final double TREND_THRESHOLD = 0.05; // 5% change

    /**
     * Raw vote counts per election -> candidateId -> count
     */
    private final Map<Long, Map<Long, Integer>> counts = new ConcurrentHashMap<>();

    /**
     * Historic snapshots for trend and anomaly detection.
     */
    @Getter
    private final Map<Long, List<Snapshot>> history = new ConcurrentHashMap<>();

    public void aggregateEvent(String messageJson) {
        try {
            VoteEvent event = mapper.readValue(messageJson, VoteEvent.class);
            counts.computeIfAbsent(event.getElectionId(), k -> new ConcurrentHashMap<>());
            Map<Long, Integer> electionCounts = counts.get(event.getElectionId());

            // Increment candidate or party count
            Long key = event.getCandidateId() != null ? event.getCandidateId() : event.getPartyId();
            electionCounts.merge(key, 1, Integer::sum);

            // Create a new snapshot after each event
            Snapshot snap = new Snapshot(System.currentTimeMillis(), new HashMap<>(electionCounts));
            history.computeIfAbsent(event.getElectionId(), k -> new ArrayList<>()).add(snap);
        } catch (Exception e) {
            // Log JSON parsing errors
            e.printStackTrace();
        }
    }

    /**
     * Builds a feature vector: [ totalVotes, countCandidate1, countCandidate2, ... ]
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
        return Nd4j.create(arr).reshape(1, arr.length);
    }

    /**
     * Simple JSON holder for vote events.
     */
    private static class VoteEvent {
        // getters/setters omitted for brevity
        @Getter
        private Long electionId;
        @Getter
        private Long candidateId;
        @Getter
        private Long partyId;
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
