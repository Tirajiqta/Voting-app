package com.example.android.dao

import android.database.sqlite.SQLiteDatabase
import com.example.android.db.orm.DbApi
import com.example.android.entity.MunicipalityEntity

class MunicipalityDao(private val db: SQLiteDatabase) {
    fun insert(municipality: MunicipalityEntity) = DbApi.insert(db, municipality)

    fun update(municipality: MunicipalityEntity) = DbApi.update(db, municipality)

    fun delete(municipality: MunicipalityEntity) = DbApi.delete(db, municipality)

    fun getAll(): List<MunicipalityEntity> = DbApi.queryAll(db, MunicipalityEntity::class)

    fun findById(id: Long): MunicipalityEntity? {
        return DbApi.queryWhere(db, MunicipalityEntity::class, "id = ?", arrayOf(id.toString())).firstOrNull()
    }
}