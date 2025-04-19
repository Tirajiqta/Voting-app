package com.example.android.dao

import android.database.sqlite.SQLiteDatabase
import com.example.android.db.orm.DbApi
import com.example.android.entity.LocationEntity

class LocationDao(private val db: SQLiteDatabase) {
    fun insert(location: LocationEntity) = DbApi.insert(db, location)

    fun update(location: LocationEntity) = DbApi.update(db, location)

    fun delete(location: LocationEntity) = DbApi.delete(db, location)

    fun getAll(): List<LocationEntity> = DbApi.queryAll(db, LocationEntity::class)

    fun findById(id: Long): LocationEntity? {
        return DbApi.queryWhere(db, LocationEntity::class, "id = ?", arrayOf(id.toString())).firstOrNull()
    }
}