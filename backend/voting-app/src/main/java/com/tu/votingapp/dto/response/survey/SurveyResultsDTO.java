package com.tu.votingapp.dto.response.survey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurveyResultsDTO {
    private Long surveyId;
    private List<SurveyQuestionResultDTO> questions;
}

