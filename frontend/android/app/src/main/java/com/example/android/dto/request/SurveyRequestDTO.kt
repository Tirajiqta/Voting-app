package com.example.android.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class SurveyRequestDTO(
    val id: Long? = null,
    val title: String,
    val description: String?,
    val startDate: String,
    val endDate: String,
    val status: String,
    val appVersion: String
)