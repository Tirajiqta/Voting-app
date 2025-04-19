package com.example.android.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val token: String
)