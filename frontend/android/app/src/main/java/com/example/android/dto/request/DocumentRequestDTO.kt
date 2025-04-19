package com.example.android.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class DocumentRequestDTO(
    val number: String,
    val validFrom: String,
    val validTo: String,
    val issuer: String,
    val gender: Int,
    val dateOfBirth: String,
    val permanentAddress: String
)