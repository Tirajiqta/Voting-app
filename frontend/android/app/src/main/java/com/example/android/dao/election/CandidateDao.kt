package com.example.android.dao.election

import com.example.android.entity.election.CandidateEntity
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface CandidateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(candidates: List<CandidateEntity>)

    @Query("SELECT * FROM candidates")
    fun getAll(): List<CandidateEntity>
    @Query("SELECT * FROM candidates WHERE id = :id")
    fun getById(id: Long): CandidateEntity?
    @Query("SELECT * FROM candidates WHERE election_id = :electionId")
    fun getByElectionId(electionId: Long): List<CandidateEntity>
    @Query("SELECT * FROM candidates WHERE party_id = :partyId")
    fun getByPartyId(partyId: Long): List<CandidateEntity>
    @Query("SELECT * FROM candidates WHERE party_id = :partyId AND election_id = :electionId")
    fun getByPartyIdAndElectionId(partyId: Long, electionId: Long): CandidateEntity?
    @Query("DELETE FROM candidates WHERE id = :id")
    fun deleteById(id: Long)

    @Insert
    fun insert(candidate: CandidateEntity)

    @Update
    fun update(candidate: CandidateEntity)
    @Delete
    fun delete(candidate: CandidateEntity)
}
