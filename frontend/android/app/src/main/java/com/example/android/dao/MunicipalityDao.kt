package com.example.android.dao

import com.example.android.entity.MunicipalityEntity
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface MunicipalityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(municipality: MunicipalityEntity): Long

    @Update
    fun update(municipality: MunicipalityEntity): Int

    @Delete
    fun delete(municipality: MunicipalityEntity): Int

    @Query("SELECT * FROM Municipality")
    fun getAll(): List<MunicipalityEntity>

    @Query("SELECT * FROM Municipality WHERE id = :id")
    fun findById(id: Long): MunicipalityEntity?
}