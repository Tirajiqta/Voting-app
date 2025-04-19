package com.example.android.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class CandidateRequestDTO(
    val id: Long? = null,
    val electionId: Long,
    val name: String,
    val bio: String?,
    val imageUri: String?,
    val position: String?,
    val appVersion: String
)