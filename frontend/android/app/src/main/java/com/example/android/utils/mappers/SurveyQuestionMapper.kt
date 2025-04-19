package com.example.android.utils.mappers

import com.example.android.dto.response.survey.SurveyOptionDTO
import com.example.android.dto.response.survey.SurveyQuestionDTO
import com.example.android.entity.survey.SurveyQuestionEntity

object SurveyQuestionMapper {
    fun SurveyQuestionDTO.toEntity(surveyId: Long): SurveyQuestionEntity = SurveyQuestionEntity(
        id = this.id,
        questionText = this.questionText,
        surveyId = surveyId
    )

    fun SurveyQuestionEntity.toDto(options: List<SurveyOptionDTO>): SurveyQuestionDTO = SurveyQuestionDTO(
        id = this.id!!,
        questionText = this.questionText,
        options = options
    )
}