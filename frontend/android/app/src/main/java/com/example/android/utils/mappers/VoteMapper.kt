package com.example.android.utils.mappers

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.android.dto.response.elections.VoteResponseDTO
import com.example.android.entity.election.VoteEntity
import java.time.LocalDateTime

object VoteMapper {
    fun VoteResponseDTO.toEntity(): VoteEntity = VoteEntity(
        id = this.id,
        userId = this.userId,
        electionId = this.electionId,
        candidateId = this.candidateId,
        partyId = this.partyId,
        voteTimestamp = this.voteTimestamp.toString()
    )

    @RequiresApi(Build.VERSION_CODES.O)
    fun VoteEntity.toDto(): VoteResponseDTO = VoteResponseDTO(
        id = this.id!!,
        userId = this.userId,
        electionId = this.electionId,
        candidateId = this.candidateId,
        partyId = this.partyId,
        voteTimestamp = LocalDateTime.parse(this.voteTimestamp).toString()
    )
}