package com.example.android.dto.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class RoleDTO(
    val id: Long? = null,
    val name: String,
    @Transient val permissions: List<PermissionDTO> = emptyList()
)