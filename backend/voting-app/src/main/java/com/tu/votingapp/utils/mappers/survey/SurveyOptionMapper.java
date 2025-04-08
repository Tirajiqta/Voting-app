package com.tu.votingapp.utils.mappers.survey;


import com.tu.votingapp.dto.general.survey.SurveyOptionDTO;
import com.tu.votingapp.entities.surveys.SurveyOptionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface SurveyOptionMapper {

    // Convert SurveyOptionEntity to SurveyOptionDTO:
    // Extract the id of the associated question into questionId.
    @Mapping(source = "question.id", target = "questionId")
    SurveyOptionDTO toDto(SurveyOptionEntity entity);

    // Convert SurveyOptionDTO to SurveyOptionEntity:
    // Use the custom mapping method to convert questionId into a SurveyQuestionsEntity.
    @Mapping(source = "questionId", target = "question", qualifiedByName = "mapQuestionFromId")
    SurveyOptionEntity toEntity(SurveyOptionDTO dto);

    // Custom method to map a Long (questionId) to a SurveyQuestionsEntity.
    @Named("mapQuestionFromId")
    default com.tu.votingapp.entities.surveys.SurveyQuestionsEntity mapQuestionFromId(Long questionId) {
        if (questionId == null) {
            return null;
        }
        com.tu.votingapp.entities.surveys.SurveyQuestionsEntity question = new com.tu.votingapp.entities.surveys.SurveyQuestionsEntity();
        question.setId(questionId);
        return question;
    }
}

