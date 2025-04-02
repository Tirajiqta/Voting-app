package com.tu.votingapp.dto.response.survey;

import com.tu.votingapp.enums.SurveyStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SurveyDTO {
    private Long id;
    private String title;
    private String description;
    private Date startDate;
    private Date endDate;
    // Representing the enum value as a String; adjust if needed.
    private SurveyStatus status;
    // List of nested survey questions
    private List<SurveyQuestionDTO> questions;
    // Reference to the ID of the user who created the survey
    private Long createdById;
}
