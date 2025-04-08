package com.tu.votingapp.utils.mappers.election;

import com.tu.votingapp.dto.general.elections.PartyDTO;
import com.tu.votingapp.entities.elections.PartyEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {CandidateMapper.class})
public interface PartyMapper {
    PartyDTO toDto(PartyEntity entity);
    PartyEntity toEntity(PartyDTO dto);
}
