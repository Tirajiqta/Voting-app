package com.example.android.dao

import android.database.sqlite.SQLiteDatabase
import com.example.android.entity.UserEntity

class UserDao(db: SQLiteDatabase) : GenericDao<UserEntity>(db, UserEntity::class){

}
