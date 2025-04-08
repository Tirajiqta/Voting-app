package com.tu.votingapp.utils.mappers;

import com.tu.votingapp.dto.general.RoleDTO;
import com.tu.votingapp.entities.RoleEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleDTO toDto(RoleEntity roleEntity);

    RoleEntity toEntity(RoleDTO roleDTO);
}
