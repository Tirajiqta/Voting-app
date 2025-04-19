package com.example.android.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val egn: String,
    val documentNumber: String
)
