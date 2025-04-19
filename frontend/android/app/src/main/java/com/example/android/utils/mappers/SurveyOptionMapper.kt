package com.example.android.utils.mappers

import com.example.android.dto.response.survey.SurveyOptionDTO
import com.example.android.entity.survey.SurveyOptionEntity

object SurveyOptionMapper {
    fun SurveyOptionDTO.toEntity(): SurveyOptionEntity = SurveyOptionEntity(
        id = this.id,
        optionText = this.optionText,
        voteCount = this.voteCount,
        questionId = this.questionId
    )

    fun SurveyOptionEntity.toDto(): SurveyOptionDTO = SurveyOptionDTO(
        id = this.id!!,
        optionText = this.optionText,
        voteCount = this.voteCount,
        questionId = this.questionId
    )
}