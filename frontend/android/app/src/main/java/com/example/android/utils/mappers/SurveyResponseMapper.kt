package com.example.android.utils.mappers

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.android.dto.response.survey.SurveyResponseDTO
import com.example.android.entity.survey.SurveyEntity
import com.example.android.entity.survey.SurveyResponseEntity
import java.time.LocalDateTime

object SurveyResponseMapper {
    fun SurveyResponseDTO.toEntity(): SurveyResponseEntity = SurveyResponseEntity(
        id = this.id,
        userId = this.userId,
        questionId = this.questionId,
        optionId = this.optionId,
        respondedAt = this.respondedAt
    )

    @RequiresApi(Build.VERSION_CODES.O)
    fun SurveyResponseEntity.toDto(surveyEntity: SurveyEntity): SurveyResponseDTO = SurveyResponseDTO(
        id = this.id!!,
        userId = this.userId,
        questionId = this.questionId,
        optionId = this.optionId,
        respondedAt = LocalDateTime.parse(this.respondedAt).toString(),
        surveyId = surveyEntity.id!!
    )
}