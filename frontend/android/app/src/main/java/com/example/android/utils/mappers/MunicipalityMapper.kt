package com.example.android.utils.mappers

import com.example.android.dto.response.MunicipalityResponseDTO
import com.example.android.entity.MunicipalityEntity
import com.example.android.entity.RegionEntity
import com.example.android.utils.mappers.RegionMapper.toDto

object MunicipalityMapper {
    fun MunicipalityResponseDTO.toEntity(): MunicipalityEntity = MunicipalityEntity(
        id = this.id,
        name = this.name,
        population = this.population,
        regionId = region.id
    )

    fun MunicipalityEntity.toDto(region: RegionEntity): MunicipalityResponseDTO = MunicipalityResponseDTO(
        id = this.id!!,
        name = this.name,
        population = this.population,
        region = region.toDto()
    )
}