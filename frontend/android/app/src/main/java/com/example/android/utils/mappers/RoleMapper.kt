package com.example.android.utils.mappers

import com.example.android.dto.response.RoleDTO
import com.example.android.entity.RoleEntity
import com.example.android.utils.mappers.PermissionMapper.toDto
import com.example.android.utils.mappers.PermissionMapper.toEntity

object RoleMapper {

    fun RoleDTO.toEntity(): RoleEntity = RoleEntity(
        id = this.id,
        name = this.name
    ).also { role ->
        role.permissions = this.permissions.map { it.toEntity() }
    }

    fun RoleEntity.toDto(): RoleDTO = RoleDTO(
        id = this.id!!,
        name = this.name,
        permissions = permissions.map { i -> i.toDto() }
    )
}