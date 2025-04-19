package com.example.android.dto.response.survey

import kotlinx.serialization.Serializable

@Serializable
data class SurveyOptionResultsDTO(
    val optionId: Long,
    val optionText: String,
    val voteCount: Int
)