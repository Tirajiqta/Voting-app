package com.example.android.dummymodel

data class Candidate(
    val id: Long,
    val preferenceNumber: Int, // <<< Number to display (e.g., 101, 102)
    val name: String, // Candidate name
    val partyId: Int // Links candidate to a party ballot number
)