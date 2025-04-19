package com.example.android.entity.referendum

import com.example.android.db.orm.Column
import com.example.android.db.orm.Table

@Table(name = "referendums")
data class ReferendumEntity(
    @Column(name = "id", primaryKey = true, autoIncrement = true)
    val id: Long? = null,

    @Column(name = "title")
    val title: String,

    @Column(name = "description")
    val description: String? = null,

    @Column(name = "question")
    val question: String,

    @Column(name = "start_date")
    val startDate: String, // e.g., "2025-04-20"

    @Column(name = "end_date")
    val endDate: String,

    @Column(name = "status")
    val status: String, // Enum value as string: "OPEN", "CLOSED", etc.

    @Column(name = "created_by")
    val createdById: Long
)