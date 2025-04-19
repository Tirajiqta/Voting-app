package com.example.android.utils.mappers

import com.example.android.constants.SurveyStatus
import com.example.android.dto.response.survey.SurveyDTO
import com.example.android.dto.response.survey.SurveyQuestionDTO
import com.example.android.entity.survey.SurveyEntity

object SurveyMapper {
    fun SurveyDTO.toEntity(): SurveyEntity = SurveyEntity(
        id = this.id,
        title = this.title,
        description = this.description,
        startDate = this.startDate.toString(),
        endDate = this.endDate.toString(),
        status = this.status.name,
        createdById = this.createdById!!
    )

    fun SurveyEntity.toDto(questions: List<SurveyQuestionDTO>): SurveyDTO = SurveyDTO(
        id = this.id!!,
        title = this.title,
        description = this.description,
        startDate = java.sql.Date.valueOf(this.startDate).toString(),
        endDate = java.sql.Date.valueOf(this.endDate).toString(),
        status = enumValueOf<SurveyStatus>(this.status),
        createdById = this.createdById,
        questions = questions
    )
}