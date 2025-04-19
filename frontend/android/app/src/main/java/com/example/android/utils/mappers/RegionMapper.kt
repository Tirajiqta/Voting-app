package com.example.android.utils.mappers

import com.example.android.dto.response.RegionResponseDTO
import com.example.android.entity.RegionEntity

object RegionMapper {
    fun RegionResponseDTO.toEntity(): RegionEntity = RegionEntity(
        id = this.id,
        name = this.name,
        population = this.population
    )

    fun RegionEntity.toDto(): RegionResponseDTO = RegionResponseDTO(
        id = this.id!!,
        name = this.name,
        population = this.population
    )
}