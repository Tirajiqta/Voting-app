package com.example.android.entity.survey

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "survey_responses")
data class SurveyResponseEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long? = null,

    @ColumnInfo(name = "user_id")
    val userId: Long,

    @ColumnInfo(name = "question_id")
    val questionId: Long,

    @ColumnInfo(name = "option_id")
    val optionId: Long,

    @ColumnInfo(name = "responded_at")
    val respondedAt: String // Store as ISO-8601 formatted String (e.g., "2024-04-18T10:15:30")
)