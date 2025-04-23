package com.example.android.entity.survey

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "survey_options")
data class SurveyOptionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long? = null,

    @ColumnInfo(name = "option_text") val optionText: String,
    @ColumnInfo(name = "vote_count") val voteCount: Int = 0,
    @ColumnInfo(name = "question_id") val questionId: Long
)