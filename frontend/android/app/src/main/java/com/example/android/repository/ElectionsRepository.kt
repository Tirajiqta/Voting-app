package com.example.android.repository

import com.example.android.dao.election.CandidateDao
import com.example.android.dao.election.ElectionDao
import com.example.android.dao.election.PartyDao
import com.example.android.dao.election.PartyVoteDao
import com.example.android.dao.election.VoteDao
import com.example.android.entity.election.CandidateEntity
import com.example.android.entity.election.ElectionEntity
import com.example.android.entity.election.PartyEntity
import com.example.android.entity.election.PartyVoteEntity
import com.example.android.entity.election.VoteEntity

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
}