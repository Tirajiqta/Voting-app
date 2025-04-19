package com.example.android.dao

import android.database.sqlite.SQLiteDatabase
import com.example.android.db.orm.DbApi
import com.example.android.entity.RegionEntity

class RegionDao(private val db: SQLiteDatabase) {
    fun insert(region: RegionEntity) = DbApi.insert(db, region)

    fun update(region: RegionEntity) = DbApi.update(db, region)

    fun delete(region: RegionEntity) = DbApi.delete(db, region)

    fun getAll(): List<RegionEntity> = DbApi.queryAll(db, RegionEntity::class)

    fun findById(id: Long): RegionEntity? {
        return DbApi.queryWhere(db, RegionEntity::class, "id = ?", arrayOf(id.toString())).firstOrNull()
    }
}