package com.example.android.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.android.db.orm.Column
import com.example.android.db.orm.Table

@Entity(
    tableName = "User",
    foreignKeys = [
        ForeignKey(
            entity = LocationEntity::class, // Assuming you have a LocationEntity
            parentColumns = ["id"],
            childColumns = ["locationId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "phone")
    val phone: String,

    @ColumnInfo(name = "password")
    val password: String,

    @ColumnInfo(name = "currentAddress")
    val currentAddress: String,

    @ColumnInfo(name = "locationId")
    val locationId: Long, // foreign key to LocationEntity

    @ColumnInfo(name = "egn")
    val egn: String
)