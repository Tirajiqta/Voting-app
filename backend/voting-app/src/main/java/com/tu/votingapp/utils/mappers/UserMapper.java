package com.tu.votingapp.utils.mappers;

import com.tu.votingapp.dto.general.UserDTO;
import com.tu.votingapp.entities.LocationEntity;
import com.tu.votingapp.entities.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = {LocationMapper.class})
public interface UserMapper {

    // Map UserEntity -> UserDTO using the LocationMapper for the location mapping.
    @Mapping(source = "regionId", target = "location")
    UserDTO toDto(UserEntity user);

    // Map UserDTO -> UserEntity.
    // Here, the nested LocationDTO is mapped back to a LocationEntity via the LocationMapper.
    @Mapping(source = "location", target = "regionId")
    UserEntity toEntity(UserDTO userDto);
}