package com.example.android.repository

import com.example.android.dao.LocationDao
import com.example.android.entity.LocationEntity
import com.example.android.entity.LocationRegionEntity

class LocationRepository(private val dao: LocationDao) {
    fun insert(entity: LocationEntity) = dao.insert(entity)
    fun update(entity: LocationEntity) = dao.update(entity)
    fun delete(entity: LocationEntity) = dao.delete(entity)
    fun getAll(): List<LocationEntity> = dao.getAll()
    fun getById(id: Long): LocationEntity? = dao.findById(id)
}