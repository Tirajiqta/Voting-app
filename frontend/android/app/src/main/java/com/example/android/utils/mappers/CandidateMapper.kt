package com.example.android.utils.mappers

import com.example.android.dto.response.elections.CandidateResponseDTO
import com.example.android.entity.election.CandidateEntity

object CandidateMapper {
    fun CandidateResponseDTO.toEntity(electionId: Long): CandidateEntity = CandidateEntity(
        id = this.id,
        name = this.name,
        bio = this.bio,
        electionId = electionId,
        votesCount = this.votesCount,
        imageUri = this.imageUri,
        position = this.position
    )

    fun CandidateEntity.toDto(): CandidateResponseDTO = CandidateResponseDTO(
        id = this.id!!,
        name = this.name,
        bio = this.bio,
        electionId = this.electionId,
        votesCount = this.votesCount,
        imageUri = this.imageUri,
        position = this.position,
        //partyId = this.partyId
    )
}