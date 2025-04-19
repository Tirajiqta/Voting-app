package com.example.android.dto.response.elections

import kotlinx.serialization.Serializable

@Serializable
data class PartyResponseDTO(
    val id: Long,
    val name: String,
    val abbreviation: String,
    val logoUrl: String,
    val leaderName: String,
    val candidates: List<CandidateResponseDTO>
)