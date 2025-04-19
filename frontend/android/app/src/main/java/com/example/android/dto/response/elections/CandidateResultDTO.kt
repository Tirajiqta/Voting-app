package com.example.android.dto.response.elections

import kotlinx.serialization.Serializable

@Serializable
data class CandidateResultDTO(
    val candidateId: Long,
    val candidateName: String,
    val votesCount: Int
)