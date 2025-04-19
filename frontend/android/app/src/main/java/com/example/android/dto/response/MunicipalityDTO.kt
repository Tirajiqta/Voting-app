package com.example.android.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class MunicipalityDTO(
    val id: Long? = null,
    val name: String,
    val population: Long,
    val region: RegionDTO
)