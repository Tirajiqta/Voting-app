package com.tu.votingapp.dto.response.survey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurveyOptionResultsDTO {
    private Long optionId;
    private String optionText;
    private int voteCount;
}

