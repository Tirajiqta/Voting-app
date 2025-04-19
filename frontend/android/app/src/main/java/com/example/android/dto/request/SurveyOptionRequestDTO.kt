package com.example.android.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class SurveyOptionRequestDTO(
    val id: Long? = null,               // null when creating, required when updating
    val questionId: Long,
    val optionText: String,
    val appVersion: String
)