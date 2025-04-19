package com.example.android.dao.survey

import android.database.sqlite.SQLiteDatabase
import com.example.android.dao.GenericDao
import com.example.android.entity.survey.SurveyOptionEntity

class SurveyOptionDao(db: SQLiteDatabase) : GenericDao<SurveyOptionEntity>(db, SurveyOptionEntity::class)
