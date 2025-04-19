package com.example.android.dto.response.elections

import kotlinx.serialization.Serializable

@Serializable
data class ElectionResultsDTO(
    val electionId: Long,
    val candidateResults: List<CandidateResultDTO>,
    val partyResults: List<PartyResultDTO>
)