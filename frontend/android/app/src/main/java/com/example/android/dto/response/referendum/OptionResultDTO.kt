package com.example.android.dto.response.referendum

import kotlinx.serialization.Serializable

@Serializable
data class OptionResultDTO(
    val optionId: Long,
    val optionText: String,
    val voteCount: Int
)