package com.example.android.dao.election

import com.example.android.entity.election.ElectionEntity
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ElectionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(election: ElectionEntity)
    @Update
    suspend fun update(election: ElectionEntity)
    @Delete
    suspend fun delete(election: ElectionEntity)
    @Query("SELECT * FROM elections")
    suspend fun getAll(): List<ElectionEntity>
    @Query("SELECT * FROM elections WHERE id = :id")
    suspend fun getById(id: Long): ElectionEntity?
}
