package com.example.android.entity.survey

import com.example.android.db.orm.Column
import com.example.android.db.orm.Table

@Table(name = "survey_options")
data class SurveyOptionEntity(
    @Column(name = "id", primaryKey = true, autoIncrement = true)
    val id: Long? = null,

    @Column(name = "option_text")
    val optionText: String,

    @Column(name = "vote_count")
    val voteCount: Int = 0,

    @Column(name = "question_id")
    val questionId: Long
)