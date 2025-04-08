package com.tu.votingapp.utils.mappers;

import com.tu.votingapp.dto.general.MunicipalityDTO;
import com.tu.votingapp.entities.MunicipalityEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {RegionMapper.class})
public interface MunicipalityMapper {
    MunicipalityDTO toDto(MunicipalityEntity entity);
    MunicipalityEntity toEntity(MunicipalityDTO dto);
}
