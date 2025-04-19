package com.example.android.entity.survey

import com.example.android.db.orm.Column
import com.example.android.db.orm.Table

@Table(name = "survey_responses")
data class SurveyResponseEntity(
    @Column(name = "id", primaryKey = true, autoIncrement = true)
    val id: Long? = null,

    @Column(name = "user_id")
    val userId: Long,

    @Column(name = "question_id")
    val questionId: Long,

    @Column(name = "option_id")
    val optionId: Long,

    @Column(name = "responded_at")
    val respondedAt: String // Store as ISO-8601 formatted String (e.g., "2024-04-18T10:15:30")
)