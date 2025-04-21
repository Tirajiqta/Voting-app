package com.tu.votingapp.utils.mappers.election;

import com.tu.votingapp.dto.general.elections.VoteDTO;
import com.tu.votingapp.entities.elections.VoteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VoteMapper {

    // Map VoteEntity -> VoteDTO by extracting election.id
    @Mapping(source = "election.id", target = "electionId")
    VoteDTO toDto(VoteEntity entity);

    // Map VoteDTO -> VoteEntity by creating a minimal ElectionEntity from electionId.
    @Mapping(target = "election", // Target field only
            // Use expression to create the ElectionEntity, accessing electionId from the dto
            // Ensure ElectionEntity has a public ElectionEntity(Long id) constructor
            // Added null check for robustness
            expression = "java(dto.getElectionId() == null ? null : new com.tu.votingapp.entities.elections.ElectionEntity(dto.getElectionId()))")

    // You might need similar mappings for voter and candidate if VoteEntity has them:
    // @Mapping(target = "voter", expression = "java(dto.getVoterId() == null ? null : new com.tu.votingapp.entities.UserEntity(dto.getVoterId()))")
    // @Mapping(target = "candidate", expression = "java(dto.getCandidateId() == null ? null : new com.tu.votingapp.entities.elections.CandidateEntity(dto.getCandidateId()))")
    VoteEntity toEntity(VoteDTO dto);
}
