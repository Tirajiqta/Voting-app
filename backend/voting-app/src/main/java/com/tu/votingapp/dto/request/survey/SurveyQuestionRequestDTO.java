package com.tu.votingapp.dto.request.survey;

import com.tu.votingapp.validation.ValidationGroups;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurveyQuestionRequestDTO {
    @Null(message = "id must be null when creating", groups = ValidationGroups.Create.class)
    @NotNull(message = "id is required when updating", groups = ValidationGroups.Update.class)
    private Long id;

    @NotNull(message = "surveyId is required")
    private Long surveyId;

    @NotBlank(message = "questionText is required")
    @Size(min = 1, max = 1000, message = "questionText must be between 1 and 1000 characters")
    private String questionText;

    @NotBlank(message = "appVersion is required")
    private String appVersion;
}
