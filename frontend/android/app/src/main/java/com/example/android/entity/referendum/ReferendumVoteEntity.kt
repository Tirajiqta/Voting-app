package com.example.android.entity.referendum

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "referendum_votes")
data class ReferendumVoteEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long? = null,

    @ColumnInfo(name = "user_id")
    val userId: Long,

    @ColumnInfo(name = "referendum_id")
    val referendumId: Long,

    @ColumnInfo(name = "option_id")
    val optionId: Long,

    @ColumnInfo(name = "vote_timestamp")
    val voteTimestamp: String // ISO 8601 format, e.g. "2025-04-20T14:22:00"
)