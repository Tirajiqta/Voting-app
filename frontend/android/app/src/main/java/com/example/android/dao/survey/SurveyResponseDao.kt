package com.example.android.dao.survey

import android.database.sqlite.SQLiteDatabase
import com.example.android.dao.GenericDao
import com.example.android.entity.survey.SurveyResponseEntity

class SurveyResponseDao(db: SQLiteDatabase) : GenericDao<SurveyResponseEntity>(db, SurveyResponseEntity::class)
