package com.example.android.entity

import com.example.android.db.orm.Column
import com.example.android.db.orm.Table

@Table(name = "Document")
data class DocumentEntity(
    @Column(name = "id", primaryKey = true, autoIncrement = true)
    val id: Long? = null,

    @Column(name = "number")
    val number: String,

    @Column(name = "validFrom")
    val validFrom: String, // Stored as ISO 8601 string: "yyyy-MM-dd"

    @Column(name = "validTo")
    val validTo: String,

    @Column(name = "issuer")
    val issuer: String,

    @Column(name = "gender")
    val gender: Int,

    @Column(name = "dateOfBirth")
    val dateOfBirth: String,

    @Column(name = "permanentAddress")
    val permanentAddress: String,

    @Column(name = "userId")
    val userId: Long // foreign key to UserEntity
)