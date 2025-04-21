package com.example.android.repository

import android.util.Log
import com.example.android.api.VotingApi
import com.example.android.dao.election.CandidateDao
import com.example.android.dao.election.ElectionDao
import com.example.android.dao.election.PartyDao
import com.example.android.dao.election.PartyVoteDao
import com.example.android.dao.election.VoteDao
import com.example.android.dto.response.PagedResponseDTO
import com.example.android.dto.response.elections.ElectionResponseDTO
import com.example.android.entity.election.CandidateEntity
import com.example.android.entity.election.ElectionEntity
import com.example.android.entity.election.PartyEntity
import com.example.android.entity.election.PartyVoteEntity
import com.example.android.entity.election.VoteEntity
import com.example.android.utils.mappers.ElectionMapper.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.IOException
import kotlin.coroutines.resume

class ElectionsRepository(
    private val electionDao: ElectionDao,
    private val candidateDao: CandidateDao,
    private val partyDao: PartyDao,
    private val partyVoteDao: PartyVoteDao,
    private val voteDao: VoteDao
) {
    // --- Election ---
    fun insertElection(election: ElectionEntity) = electionDao.insert(election)
    fun updateElection(election: ElectionEntity) = electionDao.update(election)
    fun deleteElection(election: ElectionEntity) = electionDao.delete(election)
    fun getAllElections(): List<ElectionEntity> = electionDao.getAll()
    fun getElectionById(id: Long): ElectionEntity? = electionDao.getById(id)

    // --- Candidate ---
    fun insertCandidate(candidate: CandidateEntity) = candidateDao.insert(candidate)
    fun updateCandidate(candidate: CandidateEntity) = candidateDao.update(candidate)
    fun deleteCandidate(candidate: CandidateEntity) = candidateDao.delete(candidate)
    fun getAllCandidates(): List<CandidateEntity> = candidateDao.getAll()
    fun getCandidateById(id: Long): CandidateEntity? = candidateDao.getById(id)

    // --- Party ---
    fun insertParty(party: PartyEntity) = partyDao.insert(party)
    fun updateParty(party: PartyEntity) = partyDao.update(party)
    fun deleteParty(party: PartyEntity) = partyDao.delete(party)
    fun getAllParties(): List<PartyEntity> = partyDao.getAll()
    fun getPartyById(id: Long): PartyEntity? = partyDao.getById(id)

    // --- PartyVote ---
    fun insertPartyVote(partyVote: PartyVoteEntity) = partyVoteDao.insert(partyVote)
    fun updatePartyVote(partyVote: PartyVoteEntity) = partyVoteDao.update(partyVote)
    fun deletePartyVote(partyVote: PartyVoteEntity) = partyVoteDao.delete(partyVote)
    fun getAllPartyVotes(): List<PartyVoteEntity> = partyVoteDao.getAll()
    fun getPartyVoteById(id: Long): PartyVoteEntity? = partyVoteDao.getById(id)

    // --- Vote ---
    fun insertVote(vote: VoteEntity) = voteDao.insert(vote)
    fun updateVote(vote: VoteEntity) = voteDao.update(vote)
    fun deleteVote(vote: VoteEntity) = voteDao.delete(vote)
    fun getAllVotes(): List<VoteEntity> = voteDao.getAll()
    fun getVoteById(id: Long): VoteEntity? = voteDao.getById(id)


    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun fetchAndStoreActiveElections(): Result<List<ElectionEntity>> {
        // Perform network and DB ops on IO thread
        return withContext(Dispatchers.IO) {
            // Bridge callback API to suspend function
            suspendCancellableCoroutine<Result<List<ElectionEntity>>> { continuation ->

                // Define the callback implementation
                val apiCallback = object : VotingApi.Callback<PagedResponseDTO<ElectionResponseDTO>> {
                    override fun onSuccess(response: PagedResponseDTO<ElectionResponseDTO>) {
                        try {
                            // --- Database Update Logic (inside onSuccess callback) ---
                            val electionsDto = response.content ?: emptyList()
                            val fetchedEntities = electionsDto.map { it.toEntity() }

                            fetchedEntities.forEach { entity ->
                                // Use the injected databaseRepository methods
                                val existing = getElectionById(entity.id ?: 1)
                                if (existing != null) {
                                    updateElection(entity)
                                } else {
                                    insertElection(entity)
                                }
                            }
                            // --- End Database Update Logic ---

                            // Resume the coroutine with success
                            if (continuation.isActive) { // Check if coroutine is still active
                                continuation.resume(Result.success(fetchedEntities))
                            }
                        } catch (dbException: Exception) {
                            // Handle potential DB errors during insert/update
                            if (continuation.isActive) {
                                continuation.resume(Result.failure(Exception("Database Error: ${dbException.message}", dbException)))
                            }
                        }
                    }



                    override fun onFailure(error: Throwable) {
                        if (continuation.isActive) {
                            // Wrap the original exception for context if needed, or just pass it
                            continuation.resume(
                                Result.failure(Exception("API Call Failed: ${error.message}", error)),
                                onCancellation = {
                                    if (continuation.isActive) {
                                        // Wrap the original exception for context if needed, or just pass it
                                        Log.e("ElectionsRepository", "API Call Failed: ${error.message}", error) // Add logging

                                        // Correct way to resume with Result.failure
                                        continuation.resume(Result.failure(Exception("API Call Failed: ${error.message}", error)))
                                        // REMOVE: , onCancellation = TODO() <-- This part was wrong and removed

                                    } else {
                                        // Log if the continuation is no longer active when failure occurs
                                        Log.w("ElectionsRepository", "API Call Failed but coroutine continuation was inactive: ${error.message}")
                                    }
                                }
                            )
                        }                    }
                }

                // --- Make the API Call ---
                try {
                    // Call your callback-based API function
                    VotingApi.listElections( // Use VotingApi directly if it's an object
                        page = 0,
                        size = 100, // Fetch a decent number of active elections
                        status = "ACTIVE",
                        type = null, // Pass null if type isn't required for active elections
                        callback = apiCallback
                    )

                    // Handle potential cancellation of the coroutine
                    continuation.invokeOnCancellation {
                        // If your ApiClient.get supports cancellation, trigger it here.
                        // e.g., apiClientRequest?.cancel()
                        // If not, this block might just log or do nothing.
                        println("Election fetch coroutine cancelled.")
                    }

                } catch (e: Exception) {
                    // Catch potential exceptions thrown *before* the async call starts
                    // or during the setup of the call itself (less common for simple calls)
                    if (continuation.isActive) {
                        continuation.resume(Result.failure(Exception("Error initiating API call: ${e.message}", e)))
                    }
                }
            } // End suspendCancellableCoroutine
        } // End withContext(Dispatchers.IO)
    } // End fetchAndStoreActiveElections

    // Gets all elections currently stored in the database
    // Consider adding a method like getActiveElectionsFromDb() if needed
    suspend fun getAllElectionsFromDb(): List<ElectionEntity> {
        return withContext(Dispatchers.IO) {
            getAllElections()
        }
    }
}