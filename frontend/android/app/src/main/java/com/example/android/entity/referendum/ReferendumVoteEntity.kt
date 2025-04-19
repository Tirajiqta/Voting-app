package com.example.android.entity.referendum

import com.example.android.db.orm.Column
import com.example.android.db.orm.Table

@Table(name = "referendum_votes")
data class ReferendumVoteEntity(
    @Column(name = "id", primaryKey = true, autoIncrement = true)
    val id: Long? = null,

    @Column(name = "user_id")
    val userId: Long,

    @Column(name = "referendum_id")
    val referendumId: Long,

    @Column(name = "option_id")
    val optionId: Long,

    @Column(name = "vote_timestamp")
    val voteTimestamp: String // ISO 8601 format, e.g. "2025-04-20T14:22:00"
)