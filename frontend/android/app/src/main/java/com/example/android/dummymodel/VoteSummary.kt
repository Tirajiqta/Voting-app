package com.example.android.dummymodel

data class VoteSelectionSummary(
    // Parliament
    val parliamentPartyId: Int? = null,
    val parliamentPreferenceId: Int? = null, // Optional preference

    // President/Mayor/etc. (assuming one choice per type)
    val presidentialOptionId: Int? = null,
    // val mayorOptionId: Int? = null, // Add if needed
    // val councilOptionId: Int? = null, // Add if needed

    // Referendum
    val referendumAnswerId: Int? = null,

    // Add other election types as needed
)
