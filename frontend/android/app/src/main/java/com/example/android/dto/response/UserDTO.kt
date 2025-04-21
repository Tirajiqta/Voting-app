package com.example.android.dto.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class UserDTO(
    val id: Long? = null,
    val name: String?,
    val email: String?,
    val phone: String?,
    val password: String?,
    val currentAddress: String?,
    val locationId: LocationResponseDTO?,
    val egn: String?,
    val document: DocumentDTO? = null,
    @Transient val roles: RoleDTO? = null
)