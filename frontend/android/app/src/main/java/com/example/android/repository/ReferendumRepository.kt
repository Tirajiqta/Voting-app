package com.example.android.repository

import com.example.android.dao.referendum.*
import com.example.android.entity.referendum.*

class ReferendumRepository(
    private val referendumDao: ReferendumDao,
    private val optionDao: ReferendumOptionDao,
    private val voteDao: ReferendumVoteDao
) {

    // --- Referendum ---
    fun insertReferendum(referendum: ReferendumEntity) = referendumDao.insert(referendum)
    fun updateReferendum(referendum: ReferendumEntity) = referendumDao.update(referendum)
    fun deleteReferendum(referendum: ReferendumEntity) = referendumDao.delete(referendum)
    fun getAllReferendums(): List<ReferendumEntity> = referendumDao.getAll()
    fun getReferendumById(id: Long): ReferendumEntity? = referendumDao.getById(id)

    // --- ReferendumOption ---
    fun insertOption(option: ReferendumOptionEntity) = optionDao.insert(option)
    fun updateOption(option: ReferendumOptionEntity) = optionDao.update(option)
    fun deleteOption(option: ReferendumOptionEntity) = optionDao.delete(option)
    fun getAllOptions(): List<ReferendumOptionEntity> = optionDao.getAll()
    fun getOptionById(id: Long): ReferendumOptionEntity? = optionDao.getById(id)

    // --- ReferendumVote ---
    fun insertVote(vote: ReferendumVoteEntity) = voteDao.insert(vote)
    fun updateVote(vote: ReferendumVoteEntity) = voteDao.update(vote)
    fun deleteVote(vote: ReferendumVoteEntity) = voteDao.delete(vote)
    fun getAllVotes(): List<ReferendumVoteEntity> = voteDao.getAll()
    fun getVoteById(id: Long): ReferendumVoteEntity? = voteDao.getById(id)
}