package com.tu.votingapp.utils.mappers.survey;

import com.tu.votingapp.dto.general.survey.SurveyQuestionDTO;
import com.tu.votingapp.entities.surveys.SurveyQuestionsEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {SurveyOptionMapper.class})
public interface SurveyQuestionMapper {

    SurveyQuestionDTO toDto(SurveyQuestionsEntity entity);

    SurveyQuestionsEntity toEntity(SurveyQuestionDTO dto);
}
