package com.tu.votingapp.utils.mappers.election;

import com.tu.votingapp.dto.general.elections.CandidateDTO;
import com.tu.votingapp.entities.elections.CandidateEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CandidateMapper {

    // Map CandidateEntity -> CandidateDTO
    @Mapping(source = "election.id", target = "electionId")
    CandidateDTO toDto(CandidateEntity entity);

    // Map CandidateDTO -> CandidateEntity
    @Mapping(target = "id", ignore = true) // Usually ignore ID
    @Mapping(target = "election",
            expression = "java(dto.getElectionId() == null ? null : new com.tu.votingapp.entities.elections.ElectionEntity(dto.getElectionId()))")
    // FIX: Ignore party, service sets it
    @Mapping(target = "party", ignore = true)
    // Add ignore for votesCount if it shouldn't be mapped from DTO
    @Mapping(target = "votesCount", ignore = true)
    CandidateEntity toEntity(CandidateDTO dto);
}
