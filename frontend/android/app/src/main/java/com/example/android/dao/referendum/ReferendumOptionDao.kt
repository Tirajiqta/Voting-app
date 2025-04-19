package com.example.android.dao.referendum

import android.database.sqlite.SQLiteDatabase
import com.example.android.dao.GenericDao
import com.example.android.entity.referendum.ReferendumOptionEntity

class ReferendumOptionDao(db: SQLiteDatabase) : GenericDao<ReferendumOptionEntity>(db, ReferendumOptionEntity::class)
