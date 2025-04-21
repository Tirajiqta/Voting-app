package com.example.android.dummymodel

data class UserProfile(
    val fullName: String,
    val egn: String, // Unique Citizen Number
    val idCardNumber: String,
    val address: String,
    // Optional: Add fields for existing image URLs/paths if loaded from backend
    val idCardFrontUrl: String? = null,
    val idCardBackUrl: String? = null
)