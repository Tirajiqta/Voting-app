package com.example.android.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class ReferendumVoteRequestDTO(
    val referendumId: Long,
    val optionId: Long,
    val appVersion: String
)