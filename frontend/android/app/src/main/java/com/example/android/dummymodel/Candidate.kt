package com.example.android.dummymodel

data class Candidate(
    val id: Int, // Preference number (101, 102...)
    val name: String, // Candidate name
    val partyId: Int // Links candidate to a party ballot number
)