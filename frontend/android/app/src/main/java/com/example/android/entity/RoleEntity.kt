package com.example.android.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.android.db.orm.Column
import com.example.android.db.orm.Table

@Entity(tableName = "Role")
data class RoleEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @Ignore
    var permissions: List<PermissionEntity> = emptyList()
)