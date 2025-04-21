package com.example.android.dao.election

import com.example.android.entity.election.PartyVoteEntity
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface PartyVoteDao {
    @Query("SELECT * FROM party_votes")
    fun getAll(): List<PartyVoteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg partyVote: PartyVoteEntity)
    @Query("SELECT * FROM party_votes WHERE id = :id")
    fun getById(id: Long): PartyVoteEntity?
    @Query("DELETE FROM party_votes WHERE id = :id")
    fun deleteById(id: Long)
    @Insert
    fun insert(partyVote: PartyVoteEntity)
    @Delete
    fun delete(partyVote: PartyVoteEntity)

    @Query("SELECT * FROM party_votes WHERE party_id = :partyId")
    fun getByPartyId(partyId: Long): List<PartyVoteEntity>
    @Query("DELETE FROM party_votes WHERE party_id = :partyId")
    fun deleteByPartyId(partyId: Long)
    @Update
    fun update(partyVote: PartyVoteEntity)
}
