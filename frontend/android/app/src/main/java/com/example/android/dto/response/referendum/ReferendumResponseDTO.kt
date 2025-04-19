package com.example.android.dto.response.referendum

import kotlinx.serialization.Serializable

@Serializable
data class ReferendumResponseDTO(
    val id: Long,
    val title: String,
    val description: String?,
    val question: String,
    val startDate: String,
    val endDate: String,
    val status: String,
    val createdById: Long,
    val options: List<OptionResponseDTO>
)