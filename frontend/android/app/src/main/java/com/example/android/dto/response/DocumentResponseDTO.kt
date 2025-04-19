package com.example.android.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class DocumentResponseDTO(
    val id: Long,
    val number: String,
    val validFrom: String,
    val validTo: String,
    val issuer: String,
    val gender: Int,
    val dateOfBirth: String,
    val permanentAddress: String,
    val userId: Long
)