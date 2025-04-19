package com.example.android.utils.mappers

import com.example.android.dto.response.referendum.OptionResponseDTO
import com.example.android.dto.response.referendum.ReferendumResponseDTO
import com.example.android.entity.referendum.ReferendumEntity

object ReferendumMapper {
    fun ReferendumResponseDTO.toEntity(): ReferendumEntity = ReferendumEntity(
        id = this.id,
        title = this.title,
        description = this.description,
        question = this.question,
        startDate = this.startDate,
        endDate = this.endDate,
        status = this.status,
        createdById = this.createdById
    )

    fun ReferendumEntity.toDto(options: List<OptionResponseDTO>): ReferendumResponseDTO = ReferendumResponseDTO(
        id = this.id!!,
        title = this.title,
        description = this.description,
        question = this.question,
        startDate = this.startDate,
        endDate = this.endDate,
        status = this.status,
        createdById = this.createdById,
        options = options
    )
}