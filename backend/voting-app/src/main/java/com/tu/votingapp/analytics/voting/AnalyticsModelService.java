package com.tu.votingapp.analytics.voting;

import org.deeplearning4j.nn.graph.ComputationGraph;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class AnalyticsModelService {
    private final ComputationGraph model;
    private final FeatureAggregator aggregator;
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public AnalyticsModelService(ComputationGraph analyticsModel,
                                 FeatureAggregator aggregator) {
        this.model = analyticsModel;
        this.aggregator = aggregator;
        logger.info("AnalyticsModelService initialized");
    }

    /**
     * Predict winner probabilities for each candidate (softmax output).
     */
    public double[] forecastWinners(Long electionId) {
        logger.info(() -> "forecastWinners called for electionId=" + electionId);
        INDArray features = aggregator.getFeatures(electionId);
        INDArray[] outputs = model.output(features);
        double[] probs = outputs[0].toDoubleVector();
        logger.info(() -> String.format("forecastWinners result for electionId=%d: %d probabilities", electionId, probs.length));
        return probs;
    }

    /**
     * Predict final turnout (regression output).
     */
    public double predictTurnout(Long electionId) {
        logger.info(() -> "predictTurnout called for electionId=" + electionId);
        INDArray[] outputs = model.output(aggregator.getFeatures(electionId));
        double turnout = outputs[1].getDouble(0);
        logger.info(() -> String.format("predictTurnout result for electionId=%d: %f", electionId, turnout));
        return turnout;
    }

    /**
     * Anomaly detection via threshold on feature deltas.
     */
    public List<String> detectAnomalies(Long electionId) {
        logger.info(() -> "detectAnomalies called for electionId=" + electionId);
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
                    String msg = "Candidate " + cid + " spike: " + delta + " votes";
                    anomalies.add(msg);
                    logger.fine(() -> "Anomaly detected: " + msg);
                }
            }
        } else {
            logger.fine(() -> "Not enough snapshots for anomaly detection, electionId=" + electionId);
        }
        logger.info(() -> String.format("detectAnomalies found %d anomalies for electionId=%d", anomalies.size(), electionId));
        return anomalies;
    }

    /**
     * Trend detection by comparing feature share changes over time.
     */
    public List<String> detectTrends(Long electionId) {
        logger.info(() -> "detectTrends called for electionId=" + electionId);
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
                double shareNew = (double) entry.getValue() / (totalNew == 0 ? 1 : totalNew);
                double sharePrev = (double) prev.getOrDefault(cid, 0) / (totalPrev == 0 ? 1 : totalPrev);
                double change = shareNew - sharePrev;
                if (Math.abs(change) > FeatureAggregator.TREND_THRESHOLD) {
                    String msg = String.format("Candidate %d trend change: %.2f%%", cid, change * 100);
                    trends.add(msg);
                    logger.fine(() -> "Trend detected: " + msg);
                }
            }
        } else {
            logger.fine(() -> "Not enough snapshots for trend detection, electionId=" + electionId);
        }
        logger.info(() -> String.format("detectTrends found %d trends for electionId=%d", trends.size(), electionId));
        return trends;
    }
}