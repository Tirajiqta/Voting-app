package com.example.android.dto.response.elections

import kotlinx.serialization.Serializable

@Serializable
data class PartyResultDTO(
    val partyId: Long,
    val partyName: String,
    val voteCount: Int
)