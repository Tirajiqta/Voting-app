package com.example.android.dao.election

import android.database.sqlite.SQLiteDatabase
import com.example.android.dao.GenericDao
import com.example.android.entity.election.PartyVoteEntity

class PartyVoteDao(db: SQLiteDatabase) : GenericDao<PartyVoteEntity>(db, PartyVoteEntity::class)
