package com.example.android.entity.election

import com.example.android.db.orm.Column
import com.example.android.db.orm.Table

@Table(name = "party_votes")
data class PartyVoteEntity(
    @Column(name = "id", primaryKey = true, autoIncrement = true)
    val id: Long? = null,

    @Column(name = "election_id")
    val electionId: Long,

    @Column(name = "party_id")
    val partyId: Long,

    @Column(name = "vote_count")
    val voteCount: Int
)
