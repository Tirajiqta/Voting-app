package com.tu.votingapp.utils.mappers;

import com.tu.votingapp.dto.general.UserDTO;
import com.tu.votingapp.entities.LocationEntity;
import com.tu.votingapp.entities.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = {LocationMapper.class , DocumentMapper.class})
public interface UserMapper {

    // When mapping FROM Entity TO DTO:
    @Mapping(source = "regionId", target = "locationId") // Existing location mapping
    @Mapping(target = "document", ignore = true)      // <<<--- ADD THIS LINE TO IGNORE DOCUMENT
    @Mapping(target = "password", ignore = true)      // <<<--- Also good practice: ignore password
    UserDTO toDto(UserEntity user);

    // When mapping FROM DTO TO Entity:
    // Let MapStruct handle location. Document mapping might be handled manually
    // in the service layer when creating/updating if complex logic is needed,
    // or via another @Mapping if simple. Ignoring password is correct here too.
    @Mapping(source = "locationId", target = "regionId")
    @Mapping(target = "document", ignore = true)      // Often ignored here too, set manually in service
    @Mapping(target = "password", ignore = true)      // Password should be handled/hashed separately
    @Mapping(target = "roles", ignore = true)         // Roles are usually managed via specific service methods
    UserEntity toEntity(UserDTO userDto);
}