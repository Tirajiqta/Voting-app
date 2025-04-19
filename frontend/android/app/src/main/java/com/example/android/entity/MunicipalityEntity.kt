package com.example.android.entity

import com.example.android.db.orm.Column
import com.example.android.db.orm.Table

@Table(name = "Municipality")
data class MunicipalityEntity(
    @Column(name = "id", primaryKey = true, autoIncrement = true)
    val id: Long? = null,

    @Column(name = "name")
    val name: String,

    @Column(name = "population")
    val population: Long,

    @Column(name = "regionId")
    val regionId: Long // foreign key to RegionEntity
)