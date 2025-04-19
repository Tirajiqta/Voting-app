package com.example.android.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class MunicipalityResponseDTO(
    val id: Long,
    val name: String,
    val population: Long,
    val region: RegionResponseDTO
)