package com.example.android.dao.election

import android.database.sqlite.SQLiteDatabase
import com.example.android.dao.GenericDao
import com.example.android.entity.election.CandidateEntity

class CandidateDao(db: SQLiteDatabase) : GenericDao<CandidateEntity>(db, CandidateEntity::class)
