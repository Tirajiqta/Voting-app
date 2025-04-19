package com.example.android.utils.mappers

import com.example.android.dto.response.LocationRegionResponseDTO
import com.example.android.entity.LocationEntity
import com.example.android.entity.LocationRegionEntity
import com.example.android.entity.MunicipalityEntity
import com.example.android.entity.RegionEntity
import com.example.android.utils.mappers.LocationMapper.toDto

object LocationRegionMapper {

    fun LocationRegionResponseDTO.toEntity(): LocationRegionEntity = LocationRegionEntity(
        id = this.id,
        name = this.name,
        locationId = this.location.id
    )

    fun LocationRegionEntity.toDto(location: LocationEntity, municipality: MunicipalityEntity, region: RegionEntity): LocationRegionResponseDTO = LocationRegionResponseDTO(
        id = this.id!!,
        name = this.name,
        location = location.toDto(municipality, region)
    )
}