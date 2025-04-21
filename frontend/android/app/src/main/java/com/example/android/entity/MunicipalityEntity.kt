package com.example.android.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.android.db.orm.Column
import com.example.android.db.orm.Table

@Entity(
    tableName = "Municipality",
    foreignKeys = [
        ForeignKey(
            entity = RegionEntity::class,
            parentColumns = ["id"],
            childColumns = ["regionId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MunicipalityEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "population")
    val population: Long,

    @ColumnInfo(name = "regionId")
    val regionId: Long // foreign key to RegionEntity
)