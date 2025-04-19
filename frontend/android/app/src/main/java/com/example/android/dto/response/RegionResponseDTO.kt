package com.example.android.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class RegionResponseDTO(
    val id: Long,
    val name: String,
    val population: Int
)