package com.example.android.dto.response.referendum

import kotlinx.serialization.Serializable

@Serializable
data class OptionResponseDTO(
    val id: Long,
    val optionText: String,
    val voteCount: Int,
    val referendumId: Long
)