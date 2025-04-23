package com.example.android.dao.election

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.android.entity.election.VoteEntity


data class PartyVoteResult(
    @ColumnInfo(name = "party_id") val partyId: Long,
    @ColumnInfo(name = "vote_count") val voteCount: Int,
    @ColumnInfo(name = "name") val partyName: String? // Add party name (nullable if join fails)
)

// Holds candidate results with names
data class CandidateVoteResult(
    @ColumnInfo(name = "candidate_id") val candidateId: Long,
    @ColumnInfo(name = "vote_count") val voteCount: Int,
    @ColumnInfo(name = "name") val candidateName: String? // Add candidate name (nullable if join fails)
)

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
    suspend fun insert(vote: VoteEntity)

    @Delete
    fun delete(vote: VoteEntity)


    @Query("SELECT * FROM Votes WHERE candidate_id = :candidateId")
    fun getByCandidateId(candidateId: Long): List<VoteEntity>

    @Query("SELECT COUNT(*) FROM votes WHERE user_id = :userId AND election_id = :electionId")
    suspend fun hasUserVoted(userId: Long, electionId: Long): Int

    @Update
    fun update(vote: VoteEntity)

    @Query("DELETE FROM Votes WHERE candidate_id = :candidateId")
    fun deleteByCandidateId(candidateId: Long)

    @Query("""
        SELECT v.party_id, COUNT(v.party_id) as vote_count, p.name
        FROM votes v
        LEFT JOIN parties p ON v.party_id = p.id
        WHERE v.election_id = :electionId AND v.party_id IS NOT NULL
        GROUP BY v.party_id, p.name
        ORDER BY vote_count DESC
    """)
    suspend fun getPartyVoteCountsWithName(electionId: Long): List<PartyVoteResult> // Updated return type

    @Query("""
        SELECT v.candidate_id, COUNT(v.candidate_id) as vote_count, c.name
        FROM votes v
        LEFT JOIN candidates c ON v.candidate_id = c.id
        WHERE v.election_id = :electionId AND v.candidate_id IS NOT NULL
        GROUP BY v.candidate_id, c.name
        ORDER BY vote_count DESC
    """)
    suspend fun getCandidateVoteCountsWithName(electionId: Long): List<CandidateVoteResult> // Updated return type
}
