package com.example.android.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class PermissionDTO(
    val id: Long? = null,
    val name: String?
)