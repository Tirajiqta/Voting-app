package com.example.android.dto.response.survey

import kotlinx.serialization.Serializable

@Serializable
data class SurveyQuestionResultDTO(
    val questionId: Long,
    val questionText: String,
    val options: List<SurveyOptionResultsDTO>
)