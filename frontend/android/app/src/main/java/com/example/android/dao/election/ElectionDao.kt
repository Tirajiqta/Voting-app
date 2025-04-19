package com.example.android.dao.election

import android.database.sqlite.SQLiteDatabase
import com.example.android.dao.GenericDao
import com.example.android.entity.election.ElectionEntity

class ElectionDao(db: SQLiteDatabase) : GenericDao<ElectionEntity>(db, ElectionEntity::class)
