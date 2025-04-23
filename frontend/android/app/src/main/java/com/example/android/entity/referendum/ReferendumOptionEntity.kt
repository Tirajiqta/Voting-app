package com.example.android.entity.referendum

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "referendum_options")
data class ReferendumOptionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long? = null,

    @ColumnInfo(name = "option_text")
    val optionText: String,

    @ColumnInfo(name = "vote_count")
    val voteCount: Int = 0,

    @ColumnInfo(name = "referendum_id")
    val referendumId: Long
)