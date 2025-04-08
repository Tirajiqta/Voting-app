package com.tu.votingapp.utils.mappers.survey;


import com.tu.votingapp.dto.general.survey.SurveyDTO;
import com.tu.votingapp.entities.surveys.SurveyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {SurveyQuestionMapper.class})
public interface SurveyMapper {

    // Map SurveyEntity -> SurveyDTO: extract createdBy.id to createdById.
    @Mapping(source = "createdBy.id", target = "createdById")
    SurveyDTO toDto(SurveyEntity entity);

    // Map SurveyDTO -> SurveyEntity: convert createdById into a minimal UserEntity.
    @Mapping(source = "createdById", target = "createdBy", qualifiedByName = "mapUserFromId")
    SurveyEntity toEntity(SurveyDTO dto);

    // Custom method to map a Long (user id) to a UserEntity.
    @Named("mapUserFromId")
    default com.tu.votingapp.entities.UserEntity mapUserFromId(Long userId) {
        if (userId == null) {
            return null;
        }
        com.tu.votingapp.entities.UserEntity user = new com.tu.votingapp.entities.UserEntity();
        user.setId(userId);
        return user;
    }
}