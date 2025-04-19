package com.example.android.utils.mappers

import com.example.android.dto.response.elections.PartyResponseDTO
import com.example.android.entity.election.PartyEntity

object PartyMapper {
    fun PartyResponseDTO.toEntity(): PartyEntity = PartyEntity(
        id = this.id,
        name = this.name,
        abbreviation = this.abbreviation,
        logoUrl = this.logoUrl,
        leaderName = this.leaderName
    )

    fun PartyEntity.toDto(): PartyResponseDTO = PartyResponseDTO(
        id = this.id!!,
        name = this.name,
        abbreviation = this.abbreviation!!,
        logoUrl = this.logoUrl!!,
        leaderName = this.leaderName!!,
        candidates = emptyList() // populate externally if needed
    )
}