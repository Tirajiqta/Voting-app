package com.example.android.entity.election

import com.example.android.db.orm.Column
import com.example.android.db.orm.Table

@Table(name = "elections")
data class ElectionEntity(
    @Column(name = "id", primaryKey = true, autoIncrement = true)
    val id: Long? = null,

    @Column(name = "election_name")
    val electionName: String,

    @Column(name = "description")
    val description: String? = null,

    @Column(name = "start_date")
    val startDate: String, // Use ISO date format (e.g. 2024-05-01)

    @Column(name = "end_date")
    val endDate: String,

    @Column(name = "election_type")
    val electionType: String, // Store enum as String (e.g. "PRESIDENTIAL")

    @Column(name = "status")
    val status: String, // Store enum as String (e.g. "ONGOING")

    @Column(name = "created_by")
    val createdById: Long
)