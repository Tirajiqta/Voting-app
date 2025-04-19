package com.example.android.entity.election

import com.example.android.db.orm.Column
import com.example.android.db.orm.Table


@Table(name = "votes")
data class VoteEntity(
    @Column(name = "id", primaryKey = true, autoIncrement = true)
    val id: Long? = null,

    @Column(name = "user_id")
    val userId: Long,

    @Column(name = "election_id")
    val electionId: Long,

    @Column(name = "candidate_id")
    val candidateId: Long? = null,

    @Column(name = "party_id")
    val partyId: Long? = null,

    @Column(name = "vote_timestamp")
    val voteTimestamp: String // Store as ISO 8601 string, e.g. "2025-04-19T16:00:00"
)