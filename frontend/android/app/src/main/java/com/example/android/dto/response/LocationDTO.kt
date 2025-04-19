package com.example.android.dto.response

import kotlinx.serialization.Serializable


@Serializable
data class LocationDTO(
    val id: Long? = null,
    val name: String,
    val municipality: MunicipalityDTO
)