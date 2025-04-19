package com.example.android.dao.election

import android.database.sqlite.SQLiteDatabase
import com.example.android.dao.GenericDao
import com.example.android.entity.election.VoteEntity

class VoteDao(db: SQLiteDatabase) : GenericDao<VoteEntity>(db, VoteEntity::class)
