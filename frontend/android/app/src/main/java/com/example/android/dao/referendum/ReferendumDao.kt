package com.example.android.dao.referendum

import android.database.sqlite.SQLiteDatabase
import com.example.android.dao.GenericDao
import com.example.android.entity.referendum.ReferendumEntity

class ReferendumDao(db: SQLiteDatabase) : GenericDao<ReferendumEntity>(db, ReferendumEntity::class)
