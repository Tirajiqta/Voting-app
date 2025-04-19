package com.example.android.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class SurveyQuestionRequestDTO(
    val id: Long? = null,              // null when creating, required when updating
    val surveyId: Long,
    val questionText: String,
    val appVersion: String
)