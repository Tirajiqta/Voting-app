package com.tu.votingapp.utils.mappers;

import com.tu.votingapp.dto.general.LocationRegionDTO;
import com.tu.votingapp.entities.LocationRegionEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {LocationMapper.class})
public interface LocationRegionMapper {
    LocationRegionDTO toDto(LocationRegionEntity entity);
    LocationRegionEntity toEntity(LocationRegionDTO dto);
}
