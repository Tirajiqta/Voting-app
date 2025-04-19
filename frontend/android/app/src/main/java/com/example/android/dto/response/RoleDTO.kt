package com.example.android.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class RoleDTO(
    val id: Long? = null,
    val name: String,
    val permissions: List<PermissionDTO> = emptyList()
)