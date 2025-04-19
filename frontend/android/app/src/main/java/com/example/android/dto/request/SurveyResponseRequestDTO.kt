package com.example.android.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class SurveyResponseRequestDTO(
    val surveyId: Long,
    val questionId: Long,
    val optionId: Long,
    val appVersion: String
)