package com.tu.votingapp.utils.mappers.election;

import com.tu.votingapp.dto.general.elections.PartyVoteDTO;
import com.tu.votingapp.entities.elections.PartyVoteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PartyVoteMapper {

    // Map PartyVoteEntity -> PartyVoteDTO
    @Mapping(source = "election.id", target = "electionId")
    @Mapping(source = "party.id", target = "partyId")
    // Add if PartyVoteEntity has a User voter field
    // @Mapping(source = "user.id", target = "userId")
    PartyVoteDTO toDto(PartyVoteEntity entity);

    // Map PartyVoteDTO -> PartyVoteEntity
    @Mapping(target = "id", ignore = true) // Usually ignore ID
    @Mapping(target = "election",
            expression = "java(dto.getElectionId() == null ? null : new com.tu.votingapp.entities.elections.ElectionEntity(dto.getElectionId()))")
    @Mapping(target = "party",
            expression = "java(dto.getPartyId() == null ? null : new com.tu.votingapp.entities.elections.PartyEntity(dto.getPartyId()))")
    // Add if PartyVoteEntity has a User voter field - ignore it, service sets it
    // @Mapping(target = "user", ignore = true)
    PartyVoteEntity toEntity(PartyVoteDTO dto);
}
