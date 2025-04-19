package com.example.android.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class ElectionsRequestDTO(
    val id: Long? = null,
    val electionName: String,
    val description: String?,
    val startDate: String,
    val endDate: String,
    val electionType: String,
    val status: String,
    val appVersion: String
)