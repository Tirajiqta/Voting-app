package com.tu.votingapp.utils.mappers.referendum;

import com.tu.votingapp.dto.general.referendum.ReferendumDTO;
import com.tu.votingapp.entities.referendum.ReferendumEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ReferendumOptionMapper.class})
public interface ReferendumMapper {

    // Map entity to DTO: extract createdBy.id into createdById.
    @Mapping(source = "createdBy.id", target = "createdById")
    ReferendumDTO toDto(ReferendumEntity entity);

    // Map DTO to entity: create a minimal UserEntity using the createdById.
    @Mapping(source = "createdById", target = "createdBy",
            expression = "java(new com.tu.votingapp.entities.UserEntity(createdById))")
    ReferendumEntity toEntity(ReferendumDTO dto);
}
