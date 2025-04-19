package com.example.android.utils.mappers

import com.example.android.dto.response.PermissionDTO
import com.example.android.entity.PermissionEntity

object PermissionMapper {

    fun PermissionDTO.toEntity(): PermissionEntity = PermissionEntity(
        id = this.id,
        name = this.name
    )

    fun PermissionEntity.toDto(): PermissionDTO = PermissionDTO(
        id = this.id!!,
        name = this.name
    )
}