package com.example.android.dao

import android.database.sqlite.SQLiteDatabase
import com.example.android.entity.DocumentEntity

class DocumentDao(db: SQLiteDatabase) : GenericDao<DocumentEntity>(db, DocumentEntity::class)
