package com.tu.votingapp.utils.mappers;

import com.tu.votingapp.dto.general.DocumentDTO;
import com.tu.votingapp.entities.DocumentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface DocumentMapper {
    @Mapping(target = "user", ignore = true)
    DocumentDTO toDto(DocumentEntity entity);
    DocumentEntity toEntity(DocumentDTO dto);
}
