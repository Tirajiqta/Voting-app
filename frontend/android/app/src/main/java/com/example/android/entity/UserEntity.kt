package com.example.android.entity

import com.example.android.db.orm.Column
import com.example.android.db.orm.Table

@Table(name = "User")
data class UserEntity(
    @Column(name = "id", primaryKey = true, autoIncrement = true)
    val id: Long? = null,

    @Column(name = "name")
    val name: String,

    @Column(name = "email")
    val email: String,

    @Column(name = "phone")
    val phone: String,

    @Column(name = "password")
    val password: String,

    @Column(name = "currentAddress")
    val currentAddress: String,

    @Column(name = "locationId")
    val locationId: Long, // foreign key to LocationEntity

    @Column(name = "egn")
    val egn: String
)