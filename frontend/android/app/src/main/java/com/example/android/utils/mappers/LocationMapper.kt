package com.example.android.utils.mappers

import com.example.android.dto.response.LocationResponseDTO
import com.example.android.entity.LocationEntity
import com.example.android.entity.MunicipalityEntity
import com.example.android.entity.RegionEntity
import com.example.android.utils.mappers.MunicipalityMapper.toDto

object LocationMapper {
    fun LocationResponseDTO.toEntity(): LocationEntity = LocationEntity(
        id = this.id,
        name = this.name,
        municipalityId = municipality.id
    )

    fun LocationEntity.toDto(municipality: MunicipalityEntity, region: RegionEntity): LocationResponseDTO = LocationResponseDTO(
        id = this.id!!,
        name = this.name,
        municipality = municipality.toDto(region)
    )
}