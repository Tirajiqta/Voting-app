package com.example.android.repository

import com.example.android.dao.MunicipalityDao
import com.example.android.entity.MunicipalityEntity

class MunicipalityRepository(private val dao: MunicipalityDao) {
    fun insert(municipality: MunicipalityEntity) = dao.insert(municipality)
    fun update(municipality: MunicipalityEntity) = dao.update(municipality)
    fun delete(municipality: MunicipalityEntity) = dao.delete(municipality)
    fun getAll(): List<MunicipalityEntity> = dao.getAll()
    fun getById(id: Long): MunicipalityEntity? = dao.findById(id)
}