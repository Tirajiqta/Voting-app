package com.tu.votingapp.dto.general.survey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurveyResponseDTO {
    private Long id;
    private Long userId;
    private Long surveyId;
    private Long questionId;
    private Long optionId;
    private LocalDateTime respondedAt;
}