package com.example.android.dao.election

import android.database.sqlite.SQLiteDatabase
import com.example.android.dao.GenericDao
import com.example.android.entity.election.PartyEntity

class PartyDao(db: SQLiteDatabase) : GenericDao<PartyEntity>(db, PartyEntity::class)
