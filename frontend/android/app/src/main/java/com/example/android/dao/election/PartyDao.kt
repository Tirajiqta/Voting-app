package com.example.android.dao.election

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.android.entity.election.PartyEntity

@Dao
interface PartyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(partyEntity: List<PartyEntity>)

    @Query("SELECT * FROM parties")
    suspend fun getAll(): List<PartyEntity>

    @Insert
    suspend fun insert(partyEntity: PartyEntity)

    @Update
    suspend fun update(partyEntity: PartyEntity)

    @Query("DELETE FROM parties WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Delete
    suspend fun delete(partyEntity: PartyEntity)

    @Query("SELECT * FROM parties WHERE id = :id")
    suspend fun getById(id: Long): PartyEntity?
}
