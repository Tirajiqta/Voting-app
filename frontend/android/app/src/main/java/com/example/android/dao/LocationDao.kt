package com.example.android.dao

import com.example.android.entity.LocationEntity
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(location: LocationEntity): Long

    @Update
    fun update(location: LocationEntity)

    @Delete
    fun delete(location: LocationEntity)

    @Query("SELECT * FROM Location")
    fun getAll(): List<LocationEntity>

    @Query("SELECT * FROM Location WHERE id = :id")
    fun findById(id: Long): LocationEntity?
}