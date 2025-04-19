package com.example.android.dao

import android.database.sqlite.SQLiteDatabase
import com.example.android.entity.PermissionEntity

class PermissionDao(db: SQLiteDatabase) : GenericDao<PermissionEntity>(db, PermissionEntity::class)
