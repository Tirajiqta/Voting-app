package com.tu.votingapp.dto.general.survey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SurveyQuestionDTO {
    private Long id;
    private String questionText;
    private List<SurveyOptionDTO> options;
}
