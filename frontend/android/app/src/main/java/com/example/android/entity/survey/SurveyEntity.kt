package com.example.android.entity.survey

import com.example.android.db.orm.Column
import com.example.android.db.orm.Table

@Table(name = "surveys")
data class SurveyEntity(
    @Column(name = "id", primaryKey = true, autoIncrement = true)
    val id: Long? = null,

    @Column(name = "survey_title")
    val title: String,

    @Column(name = "survey_description")
    val description: String? = null,

    @Column(name = "start_date")
    val startDate: String, // Use ISO format: "YYYY-MM-DD"

    @Column(name = "end_date")
    val endDate: String, // Use ISO format

    @Column(name = "survey_status")
    val status: String, // Store SurveyStatus as string, e.g., "ACTIVE", "INACTIVE"

    @Column(name = "created_by")
    val createdById: Long
)