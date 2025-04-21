package com.tu.votingapp.utils.mappers.referendum;

import com.tu.votingapp.dto.general.referendum.ReferendumOptionDTO;
import com.tu.votingapp.entities.referendum.ReferendumOptionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReferendumOptionMapper {

    // Map entity to DTO by extracting the referendum id.
    @Mapping(source = "referendum.id", target = "referendumId")
    ReferendumOptionDTO toDto(ReferendumOptionEntity entity);

    // Map DTO to entity using an expression to create a minimal ReferendumEntity.
    @Mapping(target = "referendum", // Target field only
            // Use expression to create the ReferendumEntity, accessing referendumId from the dto
            // Ensure ReferendumEntity has a public ReferendumEntity(Long id) constructor
            // Added null check for robustness
            expression = "java(dto.getReferendumId() == null ? null : new com.tu.votingapp.entities.referendum.ReferendumEntity(dto.getReferendumId()))")
    ReferendumOptionEntity toEntity(ReferendumOptionDTO dto);
}
