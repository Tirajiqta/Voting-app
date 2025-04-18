package com.tu.votingapp.controllers;

import com.tu.votingapp.analytics.voting.AnalyticsModelService;
import com.tu.votingapp.dto.general.analytics.AnomalyDTO;
import com.tu.votingapp.dto.general.analytics.ForecastDTO;
import com.tu.votingapp.dto.general.analytics.TrendDTO;
import com.tu.votingapp.dto.general.analytics.TurnoutDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * REST endpoints for analytics: forecasting, turnout, anomaly and trend detection.
 */
@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisController {
    private final AnalyticsModelService analyticsService;
    private final Logger logger = Logger.getLogger(AnalysisController.class.getName());

    /**
     * Winner probability forecasting for each candidate in the election.
     */
    @GetMapping("/elections/{electionId}/forecast")
    public ResponseEntity<ForecastDTO> getForecast(@PathVariable Long electionId) {
        logger.info(() -> "Forecast requested for electionId=" + electionId);
        double[] probabilities = analyticsService.forecastWinners(electionId);
        ForecastDTO dto = new ForecastDTO(electionId, probabilities);
        logger.fine(() -> "Forecast completed for electionId=" + electionId + ", probsSize=" + probabilities.length);
        return ResponseEntity.ok(dto);
    }

    /**
     * Turnout prediction for the election.
     */
    @GetMapping("/elections/{electionId}/turnout")
    public ResponseEntity<TurnoutDTO> getTurnout(@PathVariable Long electionId) {
        logger.info(() -> "Turnout prediction requested for electionId=" + electionId);
        double predicted = analyticsService.predictTurnout(electionId);
        TurnoutDTO dto = new TurnoutDTO(electionId, predicted);
        logger.fine(() -> String.format("Predicted turnout for electionId=%d: %.2f", electionId, predicted));
        return ResponseEntity.ok(dto);
    }

    /**
     * Detect anomalies in voting patterns for the election.
     */
    @GetMapping("/elections/{electionId}/anomalies")
    public ResponseEntity<List<AnomalyDTO>> getAnomalies(@PathVariable Long electionId) {
        logger.info(() -> "Anomaly detection requested for electionId=" + electionId);
        List<String> anomalies = analyticsService.detectAnomalies(electionId);
        List<AnomalyDTO> dtos = anomalies.stream()
                .map(msg -> new AnomalyDTO(electionId, msg))
                .collect(Collectors.toList());
        logger.info(() -> String.format("Anomaly detection found %d issues for electionId=%d", anomalies.size(), electionId));
        return ResponseEntity.ok(dtos);
    }

    /**
     * Detect voting trend changes for the election.
     */
    @GetMapping("/elections/{electionId}/trends")
    public ResponseEntity<List<TrendDTO>> getTrends(@PathVariable Long electionId) {
        logger.info(() -> "Trend analysis requested for electionId=" + electionId);
        List<String> trends = analyticsService.detectTrends(electionId);
        List<TrendDTO> dtos = trends.stream()
                .map(msg -> new TrendDTO(electionId, msg))
                .collect(Collectors.toList());
        logger.info(() -> String.format("Trend analysis found %d changes for electionId=%d", trends.size(), electionId));
        return ResponseEntity.ok(dtos);
    }
}