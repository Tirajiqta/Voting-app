package com.example.android.entity.survey

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "surveys")
data class SurveyEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long? = null,

    @ColumnInfo(name = "survey_title")
    val title: String,

    @ColumnInfo(name = "survey_description")
    val description: String? = null,

    @ColumnInfo(name = "start_date")
    val startDate: String,

    @ColumnInfo(name = "end_date")
    val endDate: String,
    @ColumnInfo(name = "survey_status")
    val status: String,
    @ColumnInfo(name = "created_by")
    val createdById: Long
)