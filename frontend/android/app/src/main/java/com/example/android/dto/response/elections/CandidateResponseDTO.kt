package com.example.android.dto.response.elections

import kotlinx.serialization.Serializable

@Serializable
data class CandidateResponseDTO(
    val id: Long,
    val name: String,
    val bio: String?,
    val electionId: Long,
    val votesCount: Int,
    val imageUri: String?,
    val position: String?
)