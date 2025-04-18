package com.tu.votingapp.dto.general.analytics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrendDTO {
    private Long electionId;
    private String message;
}
