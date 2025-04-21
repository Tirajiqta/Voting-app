package com.example.android.dao

import com.example.android.entity.RegionEntity
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface RegionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(region: RegionEntity): Long

    @Update
    fun update(region: RegionEntity)

    @Delete
    fun delete(region: RegionEntity)

    @Query("SELECT * FROM region")
    fun getAll(): List<RegionEntity>

    @Query("SELECT * FROM region WHERE id = :id")
    fun findById(id: Long): RegionEntity?

    @Query("SELECT COUNT(*) FROM region")
    fun getCount(): Int
}