package com.example.android.dto.response.survey

import kotlinx.serialization.Serializable

@Serializable
data class SurveyOptionDTO(
    val id: Long? = null,
    val optionText: String,
    val voteCount: Int,
    val questionId: Long
)