package com.example.android.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.android.dao.GenericDao
import com.example.android.db.orm.TableBuilder
import kotlin.reflect.KClass

class Database private constructor(context: Context) : SQLiteOpenHelper(
    context, DB_NAME, null, DB_VERSION
) {

    companion object {
        private const val DB_NAME = "app.db"
        private const val DB_VERSION = 1

        @Volatile
        private var INSTANCE: Database? = null

        fun getInstance(context: Context): Database {
            return INSTANCE ?: synchronized(this) {
                val instance = Database(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }

    private val entityClasses: List<KClass<*>> = listOf(
        // Add other entities here
    )

    override fun onCreate(db: SQLiteDatabase) {
        entityClasses.forEach { entity ->
            val createSQL = TableBuilder.createTableSQL(entity)
            db.execSQL(createSQL)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Optional: implement migration logic here
    }

    // Expose DAOs

    fun <T : Any> getDao(entityClass: KClass<T>): GenericDao<T> {
        return GenericDao(writableDatabase, entityClass)
    }

}