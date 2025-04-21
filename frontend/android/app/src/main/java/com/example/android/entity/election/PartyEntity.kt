package com.example.android.entity.election

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.android.db.orm.Column
import com.example.android.db.orm.Table

@Entity(tableName = "parties")
data class PartyEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "abbreviation")
    val abbreviation: String? = null,

    @ColumnInfo(name = "logo_url")
    val logoUrl: String? = null,

    @ColumnInfo(name = "leader_name")
    val leaderName: String? = null
)