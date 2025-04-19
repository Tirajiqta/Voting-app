package com.example.android.dummymodel

data class PresidentialPair(
    val id: Int, // Ballot number
    val partyName: String,
    // Store candidates as a single string initially based on your table,
    // or ideally parse into a List<String> when loading data
    val candidates: String // e.g., "Румен Григоров Радев\nИлиана Малинова Йотова"
)

// Define a constant for the "Support Nobody" option ID
const val SUPPORT_NOBODY_ID = 999 // Or 0, or any other invalid party ID