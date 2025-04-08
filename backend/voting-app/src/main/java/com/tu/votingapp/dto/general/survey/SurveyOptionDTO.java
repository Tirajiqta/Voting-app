package com.tu.votingapp.dto.general.survey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SurveyOptionDTO {
    private Long id;
    private String optionText;
    private int voteCount;
    private Long questionId;
}
