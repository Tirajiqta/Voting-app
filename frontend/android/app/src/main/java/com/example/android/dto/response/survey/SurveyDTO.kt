package com.example.android.dto.response.survey

import com.example.android.constants.SurveyStatus
import kotlinx.serialization.Serializable

@Serializable
data class SurveyDTO(
    val id: Long? = null,
    val title: String,
    val description: String? = null,
    val startDate: String, // use ISO-8601 format (e.g. "2025-04-19")
    val endDate: String,
    val status: SurveyStatus,
    val questions: List<SurveyQuestionDTO> = emptyList(),
    val createdById: Long? = null
)