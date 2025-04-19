package com.example.android.utils.mappers

import com.example.android.dto.response.referendum.OptionResponseDTO
import com.example.android.entity.referendum.ReferendumOptionEntity

object OptionMapper {
    fun OptionResponseDTO.toEntity(): ReferendumOptionEntity = ReferendumOptionEntity(
        id = this.id,
        optionText = this.optionText,
        voteCount = this.voteCount,
        referendumId = this.referendumId
    )

    fun ReferendumOptionEntity.toDto(): OptionResponseDTO = OptionResponseDTO(
        id = this.id!!,
        optionText = this.optionText,
        voteCount = this.voteCount,
        referendumId = this.referendumId
    )
}