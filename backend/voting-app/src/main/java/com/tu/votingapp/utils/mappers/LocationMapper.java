package com.tu.votingapp.utils.mappers;

import com.tu.votingapp.dto.general.LocationDTO;
import com.tu.votingapp.entities.LocationEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {MunicipalityMapper.class})
public interface LocationMapper {
    LocationDTO toDto(LocationEntity entity);
    LocationEntity toEntity(LocationDTO dto);
}