package com.example.android.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class DocumentDTO(
    val id: Long? = null,
    val number: String,
    val validFrom: String,        // ISO date format: "yyyy-MM-dd"
    val validTo: String,          // ISO date format: "yyyy-MM-dd"
    val issuer: String,
    val gender: Int,
    val dateOfBirth: String,      // ISO date format: "yyyy-MM-dd"
    val permanentAddress: String,
    val user: UserDTO? = null     // optional to avoid recursion issues in serialization
)