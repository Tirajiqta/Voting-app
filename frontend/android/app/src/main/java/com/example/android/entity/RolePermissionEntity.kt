package com.example.android.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.example.android.db.orm.Column
import com.example.android.db.orm.Table

@Entity(
    tableName = "RolePermission",
    primaryKeys = ["roleId", "permissionId"],
    foreignKeys = [
        ForeignKey(
            entity = RoleEntity::class, // Assuming you have a RoleEntity
            parentColumns = ["id"],
            childColumns = ["roleId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = PermissionEntity::class, // Assuming you have a PermissionEntity
            parentColumns = ["id"],
            childColumns = ["permissionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["roleId"]),
        Index(value = ["permissionId"])
    ]
)
data class RolePermissionEntity(
    @ColumnInfo(name = "roleId")
    val roleId: Long,

    @ColumnInfo(name = "permissionId")
    val permissionId: Long
)