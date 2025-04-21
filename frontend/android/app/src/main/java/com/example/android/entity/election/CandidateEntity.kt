package com.example.android.entity.election

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "candidates",
    foreignKeys = [
        ForeignKey(
            entity = ElectionEntity::class,
            parentColumns = ["id"],
            childColumns = ["election_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CandidateEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "bio")
    val bio: String? = null,

    @ColumnInfo(name = "election_id")
    val electionId: Long,

    @ColumnInfo(name = "votes_count")
    val votesCount: Int = 0,

    @ColumnInfo(name = "image_uri")
    val imageUri: String? = null,

    @ColumnInfo(name = "position")
    val position: String? = null,

    @ColumnInfo(name = "party_id")
    val partyId: Long? = null

)