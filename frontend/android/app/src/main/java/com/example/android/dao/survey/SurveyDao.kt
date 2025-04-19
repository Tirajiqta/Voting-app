package com.example.android.dao.survey

import android.database.sqlite.SQLiteDatabase
import com.example.android.dao.GenericDao
import com.example.android.entity.survey.SurveyEntity

class SurveyDao(db: SQLiteDatabase) : GenericDao<SurveyEntity>(db, SurveyEntity::class)
