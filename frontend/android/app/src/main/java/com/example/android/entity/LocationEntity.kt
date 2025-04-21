package com.example.android.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.android.db.orm.Column
import com.example.android.db.orm.Table

@Entity(
    tableName = "Location",
    foreignKeys = [
        ForeignKey(
            entity = MunicipalityEntity::class,
            parentColumns = ["id"],
            childColumns = ["municipalityId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class LocationEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "municipalityId")
    val municipalityId: Long // foreign key to MunicipalityEntity
)