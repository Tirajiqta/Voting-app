package com.example.android.utils.mappers

import com.example.android.dto.response.UserDTO
import com.example.android.entity.DocumentEntity
import com.example.android.entity.LocationEntity
import com.example.android.entity.MunicipalityEntity
import com.example.android.entity.RegionEntity
import com.example.android.entity.RoleEntity
import com.example.android.entity.UserEntity
import com.example.android.utils.mappers.DocumentMapper.toDto
import com.example.android.utils.mappers.LocationMapper.toDto
import com.example.android.utils.mappers.RoleMapper.toDto

object UserMapper {

    fun UserDTO.toEntity(): UserEntity = UserEntity(
        id = this.id?:1,
        name = this.name?:"",
        email = this.email?:"",
        phone = this.phone?:"",
        password = this.password?:"",
        currentAddress = this.currentAddress?:"",
        locationId = this.locationId?.id ?: 1L,
        egn = this.egn?:""
    )

    fun UserEntity.toDto(
        location: LocationEntity,
        municipalityEntity: MunicipalityEntity,
        regionEntity: RegionEntity,
        document: DocumentEntity?,
        roles: List<RoleEntity>
    ): UserDTO = UserDTO(
        id = this.id,
        name = this.name,
        email = this.email,
        phone = this.phone,
        password = this.password,
        currentAddress = this.currentAddress,
        egn = this.egn,
        locationId = location.toDto(
            municipality = municipalityEntity,
            region = regionEntity
        ),
        document = document?.toDto(),

        roles = roles[0].toDto()
    )
}