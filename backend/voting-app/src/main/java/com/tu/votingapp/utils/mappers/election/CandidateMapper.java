package com.tu.votingapp.utils.mappers.election;

import com.tu.votingapp.dto.general.elections.CandidateDTO;
import com.tu.votingapp.entities.elections.CandidateEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CandidateMapper {

    // Map CandidateEntity to CandidateDTO by extracting election.id.
    @Mapping(source = "election.id", target = "electionId")
    CandidateDTO toDto(CandidateEntity entity);

    // Map CandidateDTO to CandidateEntity by instantiating a minimal ElectionEntity using electionId.
    @Mapping(source = "electionId", target = "election",
            expression = "java(new com.tu.votingapp.entities.elections.ElectionEntity(electionId))")
    CandidateEntity toEntity(CandidateDTO dto);
}
