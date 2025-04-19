package com.example.android.entity.survey

import com.example.android.db.orm.Column
import com.example.android.db.orm.Table

@Table(name = "survey_questions")
data class SurveyQuestionEntity(
    @Column(name = "id", primaryKey = true, autoIncrement = true)
    val id: Long? = null,

    @Column(name = "question_text")
    val questionText: String,

    @Column(name = "survey_id")
    val surveyId: Long
)