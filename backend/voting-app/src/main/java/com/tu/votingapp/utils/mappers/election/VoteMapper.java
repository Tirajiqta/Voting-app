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
    @Mapping(source = "electionId", target = "election",
            expression = "java(new com.tu.votingapp.entities.ElectionEntity(electionId))")
    VoteEntity toEntity(VoteDTO dto);
}
