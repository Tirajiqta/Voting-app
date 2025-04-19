package com.example.android.entity.election

import com.example.android.db.orm.Column
import com.example.android.db.orm.Table

@Table(name = "candidates")
data class CandidateEntity(
    @Column(name = "id", primaryKey = true, autoIncrement = true)
    val id: Long? = null,

    @Column(name = "name")
    val name: String,

    @Column(name = "bio")
    val bio: String? = null,

    @Column(name = "election_id")
    val electionId: Long, // Foreign key to ElectionEntity

    @Column(name = "votes_count")
    val votesCount: Int = 0,

    @Column(name = "image_uri")
    val imageUri: String? = null,

    @Column(name = "position")
    val position: String? = null
)