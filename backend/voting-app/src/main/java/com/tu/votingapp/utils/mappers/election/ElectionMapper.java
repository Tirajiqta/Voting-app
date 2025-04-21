package com.tu.votingapp.utils.mappers.election;

import com.tu.votingapp.dto.general.elections.ElectionDTO;
import com.tu.votingapp.entities.elections.ElectionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CandidateMapper.class, PartyMapper.class}) // Add PartyMapper if needed for lists
public interface ElectionMapper {

    // Map ElectionEntity -> ElectionDTO
    @Mapping(source = "createdBy.id", target = "createdById")
    ElectionDTO toDto(ElectionEntity entity); // MapStruct handles lists via 'uses' if DTO has List<CandidateDTO> etc.

    // Map ElectionDTO -> ElectionEntity
    @Mapping(target = "id", ignore = true) // Usually ignore ID
    @Mapping(target = "createdBy",
            expression = "java(dto.getCreatedById() == null ? null : new com.tu.votingapp.entities.UserEntity(dto.getCreatedById()))")
    // FIX: Ignore collections managed by JPA
    @Mapping(target = "parties", ignore = true)
    @Mapping(target = "candidates", ignore = true)
    ElectionEntity toEntity(ElectionDTO dto);
}