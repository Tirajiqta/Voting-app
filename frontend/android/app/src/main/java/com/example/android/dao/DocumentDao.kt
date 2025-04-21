package com.example.android.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.android.entity.DocumentEntity

@Dao
interface DocumentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(document: DocumentEntity): Long
    @Update
    suspend fun update(document: DocumentEntity)
    @Delete
    suspend fun delete(document: DocumentEntity)
    @Query("SELECT * FROM Document WHERE id = :id")
    suspend fun getById(id: Long): DocumentEntity?
    @Query("SELECT * FROM Document")
    suspend fun getAll(): List<DocumentEntity>
}
