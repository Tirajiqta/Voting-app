package com.tu.votingapp.utils.mappers;

import com.tu.votingapp.dto.general.PermissionDTO;
import com.tu.votingapp.entities.PermissionEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    PermissionDTO toDto(PermissionEntity entity);
    PermissionEntity toEntity(PermissionDTO dto);
}