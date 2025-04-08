package com.tu.votingapp.utils.mappers.election;

import com.tu.votingapp.dto.general.elections.PartyVoteDTO;
import com.tu.votingapp.entities.elections.PartyVoteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PartyVoteMapper {

    // Map PartyVoteEntity to PartyVoteDTO.
    @Mapping(source = "election.id", target = "electionId")
    @Mapping(source = "party.id", target = "partyId")
    PartyVoteDTO toDto(PartyVoteEntity entity);

    // Map PartyVoteDTO to PartyVoteEntity.
    @Mapping(source = "electionId", target = "election",
            expression = "java(new com.tu.votingapp.entities.elections.ElectionEntity(electionId))")
    @Mapping(source = "partyId", target = "party",
            expression = "java(new com.tu.votingapp.entities.elections.PartyEntity(partyId))")
    PartyVoteEntity toEntity(PartyVoteDTO dto);
}
