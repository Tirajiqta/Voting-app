package com.example.android.repository

import com.example.android.dao.LocationRegionDao
import com.example.android.entity.LocationRegionEntity

class LocationRegionRepository(private val dao: LocationRegionDao) {
    fun insert(entity: LocationRegionEntity) = dao.insert(entity)
    fun update(entity: LocationRegionEntity) = dao.update(entity)
    fun delete(entity: LocationRegionEntity) = dao.delete(entity)
    fun getAll(): List<LocationRegionEntity> = dao.getAll()
    fun getById(id: Long): LocationRegionEntity? = dao.getById(id)
}