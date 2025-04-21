package com.tu.votingapp.utils.mappers.election;

import com.tu.votingapp.dto.general.elections.PartyDTO;
import com.tu.votingapp.entities.elections.PartyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CandidateMapper.class})
public interface PartyMapper {

    // Map PartyEntity -> PartyDTO
    @Mapping(source = "election.id", target = "electionId") // Add this if PartyDTO needs electionId
    PartyDTO toDto(PartyEntity entity);

    // Map PartyDTO -> PartyEntity
    @Mapping(target = "id", ignore = true) // Usually ignore ID
    @Mapping(target = "election", ignore = true) // FIX: Ignore election, service sets it
    @Mapping(target = "candidates", ignore = true) // Ignore collections managed by JPA
    PartyEntity toEntity(PartyDTO dto);
}
