package com.example.android.entity.election

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.android.ui.theme.viewmodels.ElectionDisplayItem

@Entity(tableName = "elections")
data class ElectionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "election_name")
    val electionName: String,

    @ColumnInfo(name = "description")
    val description: String? = null,

    @ColumnInfo(name = "start_date")
    val startDate: String, // Use ISO date format (e.g. 2024-05-01)

    @ColumnInfo(name = "end_date")
    val endDate: String,

    @ColumnInfo(name = "election_type")
    val electionType: String, // Store enum as String (e.g. "PRESIDENTIAL")

    @ColumnInfo(name = "status")
    val status: String, // Store enum as String (e.g. "ONGOING")

    @ColumnInfo(name = "created_by")
    val createdById: Long
) {
    fun toDisplayItem(): ElectionDisplayItem {
        return ElectionDisplayItem(
            id = this.id,
            name = this.electionName
        )
    }
}