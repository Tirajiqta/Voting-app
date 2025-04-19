package com.example.android.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class LocationResponseDTO(
    val id: Long,
    val name: String,
    val municipality: MunicipalityResponseDTO
)