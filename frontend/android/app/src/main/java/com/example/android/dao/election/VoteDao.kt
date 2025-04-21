package com.example.android.dao.election

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.android.entity.election.VoteEntity

@Dao
interface VoteDao {

    @Insert
    fun insertAll(vararg votes: VoteEntity)
    @Query("SELECT * FROM Votes")
    fun getAll(): List<VoteEntity>
    @Query("SELECT * FROM Votes WHERE id = :id")
    fun getById(id: Long): VoteEntity?
    @Query("DELETE FROM Votes WHERE id = :id")
    fun deleteById(id: Long)
    @Insert
    fun insert(vote: VoteEntity)

    @Delete
    fun delete(vote: VoteEntity)


    @Query("SELECT * FROM Votes WHERE candidate_id = :candidateId")
    fun getByCandidateId(candidateId: Long): List<VoteEntity>

    @Update
    fun update(vote: VoteEntity)

    @Query("DELETE FROM Votes WHERE candidate_id = :candidateId")
    fun deleteByCandidateId(candidateId: Long)

}
