package com.example.android.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import com.example.android.dto.request.VoteRequestDTO
import com.example.android.dto.response.PagedResponseDTO
import com.example.android.dto.response.elections.ElectionResponseDTO
import com.example.android.dto.response.elections.VoteResponseDTO
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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException // Good practice for exceptions

class ElectionsRepository(
    private val electionDao: ElectionDao,
    private val candidateDao: CandidateDao,
    private val partyDao: PartyDao,
    private val partyVoteDao: PartyVoteDao,
    private val voteDao: VoteDao
) {
    suspend fun insertElection(election: ElectionEntity) = electionDao.insert(election)
    suspend fun updateElection(election: ElectionEntity) = electionDao.update(election)
    suspend fun deleteElection(election: ElectionEntity) = electionDao.delete(election)
    suspend fun getAllElections(): List<ElectionEntity> = electionDao.getAll()
    suspend fun getElectionById(id: Long): ElectionEntity? = electionDao.getById(id)

    fun insertCandidate(candidate: CandidateEntity) = candidateDao.insert(candidate)
    fun updateCandidate(candidate: CandidateEntity) = candidateDao.update(candidate)
    fun deleteCandidate(candidate: CandidateEntity) = candidateDao.delete(candidate)
    fun getAllCandidates(): List<CandidateEntity> = candidateDao.getAll()
    fun getCandidateById(id: Long): CandidateEntity? = candidateDao.getById(id)

    suspend fun insertParty(party: PartyEntity) = partyDao.insert(party)
    suspend fun updateParty(party: PartyEntity) = partyDao.update(party)
    suspend fun deleteParty(party: PartyEntity) = partyDao.delete(party)
    suspend fun getAllParties(): List<PartyEntity> = partyDao.getAll()
    suspend fun getPartyById(id: Long): PartyEntity? = partyDao.getById(id)

    fun insertPartyVote(partyVote: PartyVoteEntity) = partyVoteDao.insert(partyVote)
    fun updatePartyVote(partyVote: PartyVoteEntity) = partyVoteDao.update(partyVote)
    fun deletePartyVote(partyVote: PartyVoteEntity) = partyVoteDao.delete(partyVote)
    fun getAllPartyVotes(): List<PartyVoteEntity> = partyVoteDao.getAll()
    fun getPartyVoteById(id: Long): PartyVoteEntity? = partyVoteDao.getById(id)

    suspend fun insertVote(vote: VoteEntity) = voteDao.insert(vote)
    fun updateVote(vote: VoteEntity) = voteDao.update(vote)
    fun deleteVote(vote: VoteEntity) = voteDao.delete(vote)
    fun getAllVotes(): List<VoteEntity> = voteDao.getAll()
    fun getVoteById(id: Long): VoteEntity? = voteDao.getById(id)

    suspend fun hasUserVoted(userId: Long, electionId: Long): Boolean {
        return voteDao.hasUserVoted(userId, electionId) > 0
    }

    suspend fun getPartyResults(electionId: Long): List<PartyVoteResult> {
        return voteDao.getPartyVoteCountsWithName(electionId)
    }

    suspend fun getCandidateResults(electionId: Long): List<CandidateVoteResult> {
        return voteDao.getCandidateVoteCountsWithName(electionId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun insertVoteAndCastApiVote(
        userId: Long,
        electionId: Long,
        partyId: Long?,
        candidateId: Long?
    ): Result<VoteResponseDTO> {
        return withContext(Dispatchers.IO) {
            val timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
            val voteEntity = VoteEntity(
                userId = userId,
                electionId = electionId,
                partyId = partyId,
                candidateId = candidateId,
                voteTimestamp = timestamp
            )

            try {
                Log.d("ElectionsRepository", "Attempting to insert vote locally: $voteEntity")
                val insertedVoteId = insertVote(voteEntity)
                Log.i("ElectionsRepository", "Vote inserted locally with ID: $insertedVoteId")
            } catch (dbException: Exception) {
                Log.e("ElectionsRepository", "Local database vote insertion failed", dbException)
                return@withContext Result.failure(
                    Exception("Failed to save vote locally: ${dbException.message}", dbException)
                )
            }

            suspendCancellableCoroutine<Result<VoteResponseDTO>> { continuation ->
                val requestDto = VoteRequestDTO(
                    electionId = electionId,
                    partyId = partyId,
                    candidateId = candidateId,
                    appVersion = "1.0.0"
                )

                // Define the callback for the API call
                val apiCallback = object : VotingApi.Callback<VoteResponseDTO> {
                    override fun onSuccess(response: VoteResponseDTO) {
                        Log.i("ElectionsRepository", "API vote cast successful: $response")
                        if (continuation.isActive) {
                            continuation.resume(Result.success(response))
                        }
                    }

                    override fun onFailure(error: Throwable) {
                        Log.e("ElectionsRepository", "API vote cast failed", error)
                        if (continuation.isActive) {
                            // Wrap the original error for better context
                            continuation.resumeWithException(
                                Exception("API call failed: ${error.message}", error)
                            )
                        }
                    }
                }

                try {
                    Log.d("ElectionsRepository", "Attempting to cast vote via API: $requestDto")
                    VotingApi.castVote(
                        request = requestDto,
                        callback = apiCallback
                    )

                    continuation.invokeOnCancellation {
                        Log.w("ElectionsRepository", "API vote casting coroutine cancelled.")
                    }

                } catch (apiCallException: Exception) {
                    Log.e("ElectionsRepository", "Error initiating API vote cast call", apiCallException)
                    if (continuation.isActive) {
                        continuation.resumeWithException(
                            Exception("Error initiating API call: ${apiCallException.message}", apiCallException)
                        )
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun fetchAndStoreActiveElections(): Result<List<ElectionEntity>> {
        return withContext(Dispatchers.IO) {
            suspendCancellableCoroutine<Result<List<ElectionEntity>>> { continuation ->
                val coroutineScope = this

                val apiCallback =
                    object : VotingApi.Callback<PagedResponseDTO<ElectionResponseDTO>> {
                        override fun onSuccess(response: PagedResponseDTO<ElectionResponseDTO>) {
                            coroutineScope.launch {
                                try {
                                    val electionsDto = response.content ?: emptyList()
                                    val fetchedEntities = electionsDto.map { it.toEntity() }

                                    coroutineScope {
                                        fetchedEntities.forEach { entity ->
                                            launch {
                                                try {
                                                    val existing = getElectionById(entity.id ?: 0)
                                                    if (existing != null) {
                                                        updateElection(entity)
                                                    } else {
                                                        insertElection(entity)
                                                    }
                                                } catch (dbOpException: Exception) {
                                                    Log.e("ElectionsRepository", "DB op failed for entity ${entity.id}", dbOpException)
                                                }
                                            }
                                        }
                                    }

                                    if (continuation.isActive) {
                                        continuation.resume(Result.success(fetchedEntities))
                                    }

                                } catch (e: Exception) {
                                    Log.e("ElectionsRepository", "Error processing DB operations", e)
                                    if (continuation.isActive) {
                                        continuation.resumeWithException(
                                            Exception("Database processing error: ${e.message}", e)
                                        )
                                    }
                                }
                            }
                        }

                        override fun onFailure(error: Throwable) {
                            Log.e("ElectionsRepository", "API Call Failed: ${error.message}", error)
                            if (continuation.isActive) {
                                continuation.resumeWithException(
                                    Exception("API Call Failed: ${error.message}", error)
                                )
                            }
                        }
                    }

                try {
                    VotingApi.listElections(
                        page = 0,
                        size = 100,
                        status = "ACTIVE",
                        type = null,
                        callback = apiCallback
                    )

                    continuation.invokeOnCancellation {
                        Log.d("ElectionsRepository", "Election fetch coroutine cancelled.")
                    }

                } catch (e: Exception) {
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
        return electionDao.getAll()
    }
}