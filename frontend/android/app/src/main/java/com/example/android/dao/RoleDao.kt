package com.example.android.dao

import android.database.sqlite.SQLiteDatabase
import com.example.android.entity.RoleEntity

class RoleDao(db: SQLiteDatabase) : GenericDao<RoleEntity>(db, RoleEntity::class)
