package com.example.android.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class VoteRequestDTO(
    val electionId: Long,
    val candidateId: Long?,
    val partyId: Long?,
    val appVersion: String
)