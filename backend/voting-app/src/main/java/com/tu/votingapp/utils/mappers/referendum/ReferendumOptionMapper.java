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
    @Mapping(source = "referendumId", target = "referendum",
            expression = "java(new com.tu.votingapp.entities.referendum.ReferendumEntity(referendumId))")
    ReferendumOptionEntity toEntity(ReferendumOptionDTO dto);
}
