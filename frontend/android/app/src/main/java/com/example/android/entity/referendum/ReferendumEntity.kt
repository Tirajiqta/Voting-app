package com.example.android.entity.referendum

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "referendums")
data class ReferendumEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long? = null,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "description")
    val description: String? = null,

    @ColumnInfo(name = "question")
    val question: String,

    @ColumnInfo(name = "start_date")
    val startDate: String, // e.g., "2025-04-20"

    @ColumnInfo(name = "end_date")
    val endDate: String,

    @ColumnInfo(name = "status")
    val status: String, // Enum value as string: "OPEN", "CLOSED", etc.

    @ColumnInfo(name = "created_by")
    val createdById: Long
)