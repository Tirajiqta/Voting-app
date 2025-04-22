package com.example.android.repository

import android.util.Log
// Remove this import if not used elsewhere in the file, it can clash
// import androidx.activity.result.launch
import com.example.android.api.VotingApi
import com.example.android.dao.election.CandidateDao
import com.example.android.dao.election.CandidateVoteResult
import com.example.android.dao.election.ElectionDao
import com.example.android.dao.election.PartyDao
import com.example.android.dao.election.PartyVoteDao
import com.example.android.dao.election.PartyVoteResult
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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch // Make sure you have this specific import
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException // Good practice for exceptions

class ElectionsRepository(
    private val electionDao: ElectionDao,
    private val candidateDao: CandidateDao,
    private val partyDao: PartyDao,
    private val partyVoteDao: PartyVoteDao,
    private val voteDao: VoteDao
) {
    // --- Election ---
    suspend fun insertElection(election: ElectionEntity) = electionDao.insert(election)
    suspend fun updateElection(election: ElectionEntity) = electionDao.update(election)
    suspend fun deleteElection(election: ElectionEntity) = electionDao.delete(election)
    suspend fun getAllElections(): List<ElectionEntity> = electionDao.getAll()
    suspend fun getElectionById(id: Long): ElectionEntity? = electionDao.getById(id)

    // --- Candidate (Assuming non-suspend based on original code) ---
    // If these should interact with DB asynchronously, make them suspend and use withContext
    fun insertCandidate(candidate: CandidateEntity) = candidateDao.insert(candidate)
    fun updateCandidate(candidate: CandidateEntity) = candidateDao.update(candidate)
    fun deleteCandidate(candidate: CandidateEntity) = candidateDao.delete(candidate)
    fun getAllCandidates(): List<CandidateEntity> = candidateDao.getAll()
    fun getCandidateById(id: Long): CandidateEntity? = candidateDao.getById(id)

    // --- Party ---
    suspend fun insertParty(party: PartyEntity) = partyDao.insert(party)
    suspend fun updateParty(party: PartyEntity) = partyDao.update(party)
    suspend fun deleteParty(party: PartyEntity) = partyDao.delete(party)
    suspend fun getAllParties(): List<PartyEntity> = partyDao.getAll()
    suspend fun getPartyById(id: Long): PartyEntity? = partyDao.getById(id)

    // --- PartyVote (Assuming non-suspend based on original code) ---
    fun insertPartyVote(partyVote: PartyVoteEntity) = partyVoteDao.insert(partyVote)
    fun updatePartyVote(partyVote: PartyVoteEntity) = partyVoteDao.update(partyVote)
    fun deletePartyVote(partyVote: PartyVoteEntity) = partyVoteDao.delete(partyVote)
    fun getAllPartyVotes(): List<PartyVoteEntity> = partyVoteDao.getAll()
    fun getPartyVoteById(id: Long): PartyVoteEntity? = partyVoteDao.getById(id)

    // --- Vote (Assuming non-suspend based on original code) ---
    suspend fun insertVote(vote: VoteEntity) = voteDao.insert(vote)
    fun updateVote(vote: VoteEntity) = voteDao.update(vote)
    fun deleteVote(vote: VoteEntity) = voteDao.delete(vote)
    fun getAllVotes(): List<VoteEntity> = voteDao.getAll()
    fun getVoteById(id: Long): VoteEntity? = voteDao.getById(id)

    suspend fun hasUserVoted(userId: Long, electionId: Long): Boolean {
        return voteDao.hasUserVoted(userId, electionId) > 0
    }

    suspend fun getPartyResults(electionId: Long): List<PartyVoteResult> {
        // In a real app, you might want to join this with party names here
        return voteDao.getPartyVoteCountsWithName(electionId)
    }

    suspend fun getCandidateResults(electionId: Long): List<CandidateVoteResult> {
        // In a real app, you might want to join this with candidate names here
        return voteDao.getCandidateVoteCountsWithName(electionId)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun fetchAndStoreActiveElections(): Result<List<ElectionEntity>> {
        // Perform network and DB ops on IO thread.
        // 'this' inside withContext refers to a CoroutineScope
        return withContext(Dispatchers.IO) { // <-- This provides a CoroutineScope
            suspendCancellableCoroutine<Result<List<ElectionEntity>>> { continuation ->
                // Capture the scope provided by withContext
                val coroutineScope = this

                val apiCallback =
                    object : VotingApi.Callback<PagedResponseDTO<ElectionResponseDTO>> {
                        override fun onSuccess(response: PagedResponseDTO<ElectionResponseDTO>) {
                            // Use the captured scope to launch the database operations
                            coroutineScope.launch { // <-- Launch on the captured scope
                                try {
                                    val electionsDto = response.content ?: emptyList()
                                    val fetchedEntities = electionsDto.map { it.toEntity() }

                                    // Use coroutineScope to wait for all inner DB operations to finish
                                    // This naturally inherits the context (Dispatchers.IO)
                                    coroutineScope { // <-- This is correct usage inside a coroutine
                                        fetchedEntities.forEach { entity ->
                                            // Launch each DB operation concurrently within the scope
                                            launch { // <-- This launch inherits the scope and context
                                                try {
                                                    val existing = getElectionById(entity.id ?: 0) // Use 0 as default id
                                                    if (existing != null) {
                                                        updateElection(entity)
                                                    } else {
                                                        insertElection(entity)
                                                    }
                                                } catch (dbOpException: Exception) {
                                                    // Log individual DB operation failure if needed
                                                    Log.e("ElectionsRepository", "DB op failed for entity ${entity.id}", dbOpException)
                                                    // Decide if one failure should fail the whole process
                                                    // For now, let's let others continue but log the error
                                                }
                                            }
                                        }
                                    } // <-- Waits for all inner launches to complete

                                    // All database operations attempted (some might have failed individually if not handled)
                                    // Resume the original continuation only if it's still active
                                    if (continuation.isActive) {
                                        continuation.resume(Result.success(fetchedEntities))
                                    }

                                } catch (e: Exception) { // Catch exceptions during mapping or the outer coroutineScope setup
                                    Log.e("ElectionsRepository", "Error processing DB operations", e)
                                    if (continuation.isActive) {
                                        continuation.resumeWithException(
                                            Exception("Database processing error: ${e.message}", e)
                                        )
                                    }
                                }
                            } // End of coroutineScope.launch
                        }

                        override fun onFailure(error: Throwable) {
                            Log.e("ElectionsRepository", "API Call Failed: ${error.message}", error)
                            // Resume with failure only if the continuation is still active
                            if (continuation.isActive) {
                                continuation.resumeWithException(
                                    Exception("API Call Failed: ${error.message}", error)
                                )
                            }
                            // Removed the redundant onCancellation block inside resume/resumeWithException
                        }
                    }

                // --- Make the API Call ---
                try {
                    VotingApi.listElections(
                        page = 0,
                        size = 100,
                        status = "ACTIVE",
                        type = null,
                        callback = apiCallback
                    )

                    continuation.invokeOnCancellation {
                        // If your API call can be cancelled, do it here.
                        // e.g., someApiClientRequest.cancel()
                        Log.d("ElectionsRepository", "Election fetch coroutine cancelled.")
                        // No need to resume continuation here, it's handled by the cancellation mechanism
                    }

                } catch (e: Exception) { // Catch immediate exceptions during API call setup
                    Log.e("ElectionsRepository", "Error initiating API call", e)
                    if (continuation.isActive) {
                        continuation.resumeWithException(
                            Exception("Error initiating API call: ${e.message}", e)
                        )
                    }
                }
            } // End suspendCancellableCoroutine
        } // End withContext(Dispatchers.IO)
    }

    // Gets all elections currently stored in the database
    suspend fun getAllElectionsFromDb(): List<ElectionEntity> {
        // No need for extra withContext if getAllElections already uses Dispatchers.IO implicitly or explicitly
        // Assuming electionDao.getAll() handles its own context or is safe to call from IO
        return electionDao.getAll()
        // OR if electionDao.getAll() doesn't specify context:
        // return withContext(Dispatchers.IO) {
        //     electionDao.getAll()
        // }
    }
}