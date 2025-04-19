package com.example.android.repository

import com.example.android.dao.RegionDao
import com.example.android.entity.RegionEntity

class RegionRepository(private val dao: RegionDao) {
    fun insert(region: RegionEntity) = dao.insert(region)
    fun update(region: RegionEntity) = dao.update(region)
    fun delete(region: RegionEntity) = dao.delete(region)
    fun getAll(): List<RegionEntity> = dao.getAll()
    fun getById(id: Long): RegionEntity? = dao.findById(id)
}