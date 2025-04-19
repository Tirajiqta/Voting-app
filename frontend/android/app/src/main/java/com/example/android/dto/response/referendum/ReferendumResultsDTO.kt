package com.example.android.dto.response.referendum

import kotlinx.serialization.Serializable

@Serializable
data class ReferendumResultsDTO(
    val referendumId: Long,
    val optionResults: List<OptionResultDTO>
)