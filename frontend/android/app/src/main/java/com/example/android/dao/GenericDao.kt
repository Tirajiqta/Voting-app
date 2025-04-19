package com.example.android.dao

import android.database.sqlite.SQLiteDatabase
import com.example.android.db.orm.DbApi
import kotlin.reflect.KClass

open class GenericDao<T : Any>(
    private val db: SQLiteDatabase,
    private val clazz: KClass<T>
) {
    fun insert(entity: T) = DbApi.insert(db, entity)

    fun update(entity: T) = DbApi.update(db, entity)

    fun delete(entity: T) = DbApi.delete(db, entity)

    fun getAll(): List<T> = DbApi.queryAll(db, clazz)

    fun queryWhere(where: String, args: Array<String>): List<T> = DbApi.queryWhere(db, clazz, where, args)

    fun getById(id: Long): T? {
        return queryWhere("id = ?", arrayOf(id.toString())).firstOrNull()
    }
}