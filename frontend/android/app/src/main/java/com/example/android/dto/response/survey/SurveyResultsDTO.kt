package com.example.android.dto.response.survey

import kotlinx.serialization.Serializable

@Serializable
data class SurveyResultsDTO(
    val surveyId: Long,
    val questions: List<SurveyQuestionResultDTO>
)