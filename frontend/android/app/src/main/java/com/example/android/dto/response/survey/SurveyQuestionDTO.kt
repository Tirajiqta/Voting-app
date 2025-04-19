package com.example.android.dto.response.survey

import kotlinx.serialization.Serializable

@Serializable
data class SurveyQuestionDTO(
    val id: Long? = null,
    val questionText: String,
    val options: List<SurveyOptionDTO> = emptyList()
)