package com.example.android.dao.referendum

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.android.entity.referendum.ReferendumVoteEntity

@Dao
interface ReferendumVoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vote: ReferendumVoteEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(votes: List<ReferendumVoteEntity>)

    @Query("SELECT * FROM referendum_votes WHERE id = :id")
    fun getById(id: Long): ReferendumVoteEntity?

    @Query("SELECT * FROM referendum_votes WHERE user_id = :userId")
    fun getByUserId(userId: Long): List<ReferendumVoteEntity>

}
