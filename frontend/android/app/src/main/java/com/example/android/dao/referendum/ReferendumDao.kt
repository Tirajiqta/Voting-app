package com.example.android.dao.referendum

import com.example.android.entity.referendum.ReferendumEntity


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ReferendumDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReferendum(referendum: ReferendumEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllReferendums(referendums: List<ReferendumEntity>)

    @Update
    fun updateReferendum(referendum: ReferendumEntity)

    @Delete
    fun deleteReferendum(referendum: ReferendumEntity)

    @Query("SELECT * FROM referendums WHERE id = :referendumId")
    fun getReferendumById(referendumId: Long): ReferendumEntity?

    @Query("SELECT * FROM referendums ORDER BY start_date DESC")
    fun getAllReferendumsFlow(): Flow<List<ReferendumEntity>>
    @Query("SELECT * FROM referendums ORDER BY start_date DESC")
    fun getAllReferendumsList(): List<ReferendumEntity>

    @Query("SELECT * FROM referendums WHERE status = :status ORDER BY start_date DESC")
    fun getReferendumsByStatusFlow(status: String): Flow<List<ReferendumEntity>>

    @Query("SELECT * FROM referendums WHERE status = :status ORDER BY start_date DESC")
    fun getReferendumsByStatusList(status: String): List<ReferendumEntity>


    @Query("DELETE FROM referendums")
    fun deleteAllReferendums()

}
