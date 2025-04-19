package com.example.android.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class LocationRegionResponseDTO(
    val id: Long,
    val name: String,
    val location: LocationResponseDTO
)