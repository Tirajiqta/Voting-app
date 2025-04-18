package com.tu.votingapp.dto.response.survey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurveyQuestionResultDTO {
    private Long questionId;
    private String questionText;
    private List<SurveyOptionResultsDTO> options;

}

