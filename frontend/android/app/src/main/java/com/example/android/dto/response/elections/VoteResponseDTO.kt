package com.example.android.dto.response.elections

import kotlinx.serialization.Serializable

@Serializable
data class VoteResponseDTO(
    val id: Long,
    val userId: Long,
    val electionId: Long,
    val candidateId: Long?,
    val partyId: Long?,
    val voteTimestamp: String
)