package com.example.android.entity

import com.example.android.db.orm.Column
import com.example.android.db.orm.Table

@Table(name = "RolePermission")
data class RolePermissionEntity(
    @Column(name = "roleId", primaryKey = true)
    val roleId: Long,

    @Column(name = "permissionId", primaryKey = true)
    val permissionId: Long
)