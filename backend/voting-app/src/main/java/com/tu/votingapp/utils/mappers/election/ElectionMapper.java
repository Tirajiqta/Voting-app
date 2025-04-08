package com.tu.votingapp.utils.mappers.election;

import com.tu.votingapp.dto.general.elections.ElectionDTO;
import com.tu.votingapp.entities.elections.ElectionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CandidateMapper.class})
public interface ElectionMapper {

    // Convert ElectionEntity to ElectionDTO, extracting createdBy.id.
    @Mapping(source = "createdBy.id", target = "createdById")
    ElectionDTO toDto(ElectionEntity entity);

    // Convert ElectionDTO to ElectionEntity, creating a minimal UserEntity from createdById.
    @Mapping(source = "createdById", target = "createdBy",
            expression = "java(new com.tu.votingapp.entities.UserEntity(createdById))")
    ElectionEntity toEntity(ElectionDTO dto);
}
