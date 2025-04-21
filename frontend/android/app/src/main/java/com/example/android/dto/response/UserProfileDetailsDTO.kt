package com.example.android.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class UserProfileDetailsDTO(
    val user: UserDTO, // Assuming user is always present on success based on backend logic
    val document: DocumentResponseDTO? // Document might be null if not added yet
)