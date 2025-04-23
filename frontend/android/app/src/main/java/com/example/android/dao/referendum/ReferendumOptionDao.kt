package com.example.android.dao.referendum

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.android.entity.referendum.ReferendumOptionEntity

@Dao
interface ReferendumOptionDao {
    @Insert
    fun insert(referendumOption: ReferendumOptionEntity)

    @Query("SELECT * FROM referendum_options WHERE referendum_id = :referendumId")
    fun getOptionsByReferendumId(referendumId: Long): List<ReferendumOptionEntity>

    @Query("SELECT * FROM referendum_options")
    fun getAll():List<ReferendumOptionEntity>

    @Query("DELETE FROM referendum_options")
    fun deleteAll()

    @Query("DELETE FROM referendum_options WHERE referendum_id = :referendumId")
    fun deleteByReferendumId(referendumId: Long)

    @Query("SELECT * FROM referendum_options WHERE id = :id")
    fun getById(id: Long): ReferendumOptionEntity?


}
