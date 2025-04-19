package com.example.android.dto.response.survey

import kotlinx.serialization.Serializable

@Serializable
data class SurveyResponseDTO(
    val id: Long,
    val userId: Long,
    val surveyId: Long,
    val questionId: Long,
    val optionId: Long,
    val respondedAt: String // ISO 8601 date-time string (e.g., "2025-04-19T14:30:00")
)