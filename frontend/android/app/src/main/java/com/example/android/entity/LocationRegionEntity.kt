package com.example.android.entity

import com.example.android.db.orm.Column
import com.example.android.db.orm.Table

@Table(name = "LocationRegion")
data class LocationRegionEntity(
    @Column(name = "id", primaryKey = true, autoIncrement = true)
    val id: Long? = null,

    @Column(name = "name")
    val name: String,

    @Column(name = "locationId")
    val locationId: Long // foreign key to LocationEntity
)