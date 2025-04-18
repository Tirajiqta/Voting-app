package com.tu.votingapp.dto.general.analytics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForecastDTO {
    private Long electionId;
    private double[] probabilities;
}
