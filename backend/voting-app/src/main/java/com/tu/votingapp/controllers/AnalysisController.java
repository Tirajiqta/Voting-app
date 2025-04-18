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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisController {
    private final AnalyticsModelService analyticsService;

    /**
     * Winner probability forecasting for each candidate in the election.
     */
    @GetMapping("/elections/{electionId}/forecast")
    public ResponseEntity<ForecastDTO> getForecast(@PathVariable Long electionId) {
        double[] probabilities = analyticsService.forecastWinners(electionId);
        ForecastDTO dto = new ForecastDTO(electionId, probabilities);
        return ResponseEntity.ok(dto);
    }

    /**
     * Turnout prediction for the election.
     */
    @GetMapping("/elections/{electionId}/turnout")
    public ResponseEntity<TurnoutDTO> getTurnout(@PathVariable Long electionId) {
        double predicted = analyticsService.predictTurnout(electionId);
        TurnoutDTO dto = new TurnoutDTO(electionId, predicted);
        return ResponseEntity.ok(dto);
    }

    /**
     * Detect anomalies in voting patterns for the election.
     */
    @GetMapping("/elections/{electionId}/anomalies")
    public ResponseEntity<List<AnomalyDTO>> getAnomalies(@PathVariable Long electionId) {
        List<String> anomalies = analyticsService.detectAnomalies(electionId);
        List<AnomalyDTO> dtos = anomalies.stream()
                .map(msg -> new AnomalyDTO(electionId, msg))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Detect voting trend changes for the election.
     */
    @GetMapping("/elections/{electionId}/trends")
    public ResponseEntity<List<TrendDTO>> getTrends(@PathVariable Long electionId) {
        List<String> trends = analyticsService.detectTrends(electionId);
        List<TrendDTO> dtos = trends.stream()
                .map(msg -> new TrendDTO(electionId, msg))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
