package com.example.android.utils.mappers

import com.example.android.dto.response.referendum.ReferendumVoteResponseDTO
import com.example.android.entity.referendum.ReferendumVoteEntity

object ReferendumVoteMapper {
    fun ReferendumVoteResponseDTO.toEntity(): ReferendumVoteEntity = ReferendumVoteEntity(
        id = this.id,
        userId = this.userId,
        referendumId = this.referendumId,
        optionId = this.optionId,
        voteTimestamp = this.voteTimestamp
    )

    fun ReferendumVoteEntity.toDto(): ReferendumVoteResponseDTO = ReferendumVoteResponseDTO(
        id = this.id!!,
        userId = this.userId,
        referendumId = this.referendumId,
        optionId = this.optionId,
        voteTimestamp = this.voteTimestamp
    )
}