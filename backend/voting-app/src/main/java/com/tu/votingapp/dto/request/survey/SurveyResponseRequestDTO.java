package com.tu.votingapp.dto.request.survey;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurveyResponseRequestDTO {
    @NotNull(message = "surveyId is required")
    private Long surveyId;

    @NotNull(message = "questionId is required")
    private Long questionId;

    @NotNull(message = "optionId is required")
    private Long optionId;

    @NotBlank(message = "appVersion is required")
    private String appVersion;
}
