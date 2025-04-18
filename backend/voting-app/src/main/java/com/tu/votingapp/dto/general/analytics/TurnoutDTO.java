package com.tu.votingapp.dto.general.analytics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TurnoutDTO {
    private Long electionId;
    private double predictedTurnout;
}
