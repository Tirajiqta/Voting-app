package com.example.android.dao.survey

import android.database.sqlite.SQLiteDatabase
import com.example.android.dao.GenericDao
import com.example.android.entity.survey.SurveyQuestionEntity

class SurveyQuestionDao(db: SQLiteDatabase) : GenericDao<SurveyQuestionEntity>(db, SurveyQuestionEntity::class)
