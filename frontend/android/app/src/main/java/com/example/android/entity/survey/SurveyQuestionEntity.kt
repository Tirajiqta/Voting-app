package com.example.android.entity.survey

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "survey_questions")
data class SurveyQuestionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    @ColumnInfo(name = "question_text")
    val questionText: String,
    @ColumnInfo(name = "survey_id")
    val surveyId: Long
)