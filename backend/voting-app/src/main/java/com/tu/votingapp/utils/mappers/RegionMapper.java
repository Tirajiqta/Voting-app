package com.tu.votingapp.utils.mappers;

import com.tu.votingapp.dto.general.RegionDTO;
import com.tu.votingapp.entities.RegionEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RegionMapper {
    RegionDTO toDto(RegionEntity entity);
    RegionEntity toEntity(RegionDTO dto);
}
