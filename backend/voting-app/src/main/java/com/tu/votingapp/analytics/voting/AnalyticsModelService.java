package com.tu.votingapp.analytics.voting;

import org.deeplearning4j.nn.graph.ComputationGraph;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticsModelService {
    private final ComputationGraph model;
    private final FeatureAggregator aggregator;

    public AnalyticsModelService(ComputationGraph analyticsModel,
                                 FeatureAggregator aggregator) {
        this.model = analyticsModel;
        this.aggregator = aggregator;
    }

    public double[] forecastWinners(Long electionId) {
        INDArray features = aggregator.getFeatures(electionId);
        INDArray[] outputs = model.output(features);
        return outputs[0].toDoubleVector();
    }

    public double predictTurnout(Long electionId) {
        INDArray[] outputs = model.output(aggregator.getFeatures(electionId));
        return outputs[1].getDouble(0);
    }

    public List<String> detectAnomalies(Long electionId) {
        List<FeatureAggregator.Snapshot> snaps = aggregator.getHistory().get(electionId);
        List<String> anomalies = new ArrayList<>();
        if (snaps != null && snaps.size() >= 2) {
            int last = snaps.size() - 1;
            Map<Long, Integer> newest = snaps.get(last).getCounts();
            Map<Long, Integer> prev = snaps.get(last - 1).getCounts();
            double avgIncrease = newest.values().stream()
                    .mapToInt(i -> i)
                    .average().orElse(0.0);
            for (Map.Entry<Long, Integer> entry : newest.entrySet()) {
                long cid = entry.getKey();
                int delta = entry.getValue() - prev.getOrDefault(cid, 0);
                if (delta > avgIncrease * FeatureAggregator.ANOMALY_THRESHOLD_MULTIPLIER) {
                    anomalies.add("Candidate " + cid + " spike: " + delta + " votes");
                }
            }
        }
        return anomalies;
    }

    public List<String> detectTrends(Long electionId) {
        List<FeatureAggregator.Snapshot> snaps = aggregator.getHistory().get(electionId);
        List<String> trends = new ArrayList<>();
        if (snaps != null && snaps.size() >= 2) {
            int last = snaps.size() - 1;
            Map<Long, Integer> newest = snaps.get(last).getCounts();
            Map<Long, Integer> prev = snaps.get(last - 1).getCounts();
            int totalNew = newest.values().stream().mapToInt(i -> i).sum();
            int totalPrev = prev.values().stream().mapToInt(i -> i).sum();
            for (Map.Entry<Long, Integer> entry : newest.entrySet()) {
                long cid = entry.getKey();
                double shareNew = (double) entry.getValue() / totalNew;
                double sharePrev = (double) prev.getOrDefault(cid, 0) / totalPrev;
                if (Math.abs(shareNew - sharePrev) > FeatureAggregator.TREND_THRESHOLD) {
                    trends.add("Candidate " + cid + " trend change: " + String.format("%.2f%%", (shareNew - sharePrev) * 100));
                }
            }
        }
        return trends;
    }
}
