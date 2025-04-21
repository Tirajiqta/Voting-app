package com.example.android.entity.election

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.android.db.orm.Column
import com.example.android.db.orm.Table

@Entity(
    tableName = "party_votes",
    foreignKeys = [
        ForeignKey(
            entity = ElectionEntity::class,
            parentColumns = ["id"],
            childColumns = ["election_id"],
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
data class PartyVoteEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "election_id")
    val electionId: Long,

    @ColumnInfo(name = "party_id")
    val partyId: Long,

    @ColumnInfo(name = "vote_count")
    val voteCount: Int,

    @ColumnInfo(name = "candidate_id")
    val candidateId: Long
)
