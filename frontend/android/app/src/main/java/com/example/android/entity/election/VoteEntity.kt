package com.example.android.entity.election

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.android.db.orm.Column
import com.example.android.db.orm.Table


@Entity(
    tableName = "votes",
    foreignKeys = [
        ForeignKey(
            entity = ElectionEntity::class,
            parentColumns = ["id"],
            childColumns = ["election_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CandidateEntity::class,
            parentColumns = ["id"],
            childColumns = ["candidate_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PartyEntity::class,
            parentColumns = ["id"],
            childColumns = ["party_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class VoteEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "user_id")
    val userId: Long,

    @ColumnInfo(name = "election_id")
    val electionId: Long,

    @ColumnInfo(name = "candidate_id")
    val candidateId: Long? = null,

    @ColumnInfo(name = "party_id")
    val partyId: Long? = null,

    @ColumnInfo(name = "vote_timestamp")
    val voteTimestamp: String // Store as ISO 8601 string, e.g. "2025-04-19T16:00:00"
)