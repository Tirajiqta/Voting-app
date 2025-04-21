package com.example.android.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.android.db.orm.Column
import com.example.android.db.orm.Table

@Entity(
    tableName = "Document",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "number")
    val number: String,

    @ColumnInfo(name = "validFrom")
    val validFrom: String, // Stored as ISO 8601 string: "yyyy-MM-dd"

    @ColumnInfo(name = "validTo")
    val validTo: String,

    @ColumnInfo(name = "issuer")
    val issuer: String,

    @ColumnInfo(name = "gender")
    val gender: Int,

    @ColumnInfo(name = "dateOfBirth")
    val dateOfBirth: String,

    @ColumnInfo(name = "permanentAddress")
    val permanentAddress: String,

    @ColumnInfo(name = "userId")
    val userId: Long // foreign key to UserEntity
)