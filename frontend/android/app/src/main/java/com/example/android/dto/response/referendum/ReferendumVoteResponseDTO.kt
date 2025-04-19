package com.example.android.dto.response.referendum

import kotlinx.serialization.Serializable

@Serializable
data class ReferendumVoteResponseDTO(
    val id: Long,
    val userId: Long,
    val referendumId: Long,
    val optionId: Long,
    val voteTimestamp: String
)