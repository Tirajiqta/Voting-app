package com.example.android.dto.response.elections

import kotlinx.serialization.Serializable

@Serializable
data class ElectionResponseDTO(
    val id: Long,
    val electionName: String,
    val description: String?,
    val startDate: String,
    val endDate: String,
    val electionType: String,
    val status: String,
    val createdById: Long,
    val candidates: List<CandidateResponseDTO>,
    val parties: List<PartyResponseDTO>
)