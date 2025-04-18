package com.example.android.db.orm

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.KParameter
import kotlin.reflect.full.primaryConstructor
object DbApi {

    fun <T : Any> insert(db: SQLiteDatabase, obj: T) {
        val clazz = obj::class
        val values = ContentValues()

        clazz.memberProperties.forEach { prop ->
            val col = prop.findAnnotation<Column>()
            if (col != null && !(col.primaryKey && col.autoIncrement)) {
                val value = prop.call(obj)
                when (value) {
                    is String -> values.put(col.name, value)
                    is Int -> values.put(col.name, value)
                    is Long -> values.put(col.name, value)
                    is Float -> values.put(col.name, value)
                    is Double -> values.put(col.name, value)
                    is Boolean -> values.put(col.name, if (value) 1 else 0)
                }
            }
        }

        db.insert(clazz.simpleName!!, null, values)
    }

    private fun <T> params(constructor:  KFunction<T>, args: MutableMap<KParameter, Any?>): T {
            return constructor.callBy(args)
    }

    fun <T : Any> queryAll(db: SQLiteDatabase, clazz: KClass<T>): List<T> {
        val result = mutableListOf<T>()
        val tableName = clazz.simpleName ?: throw IllegalArgumentException("Class must have a name")

        val cursor = db.query(tableName, null, null, null, null, null, null)

        val constructor = clazz.primaryConstructor
            ?: throw IllegalArgumentException("Class must have a primary constructor")


        while (cursor.moveToNext()) {
            val argMap = mutableMapOf<KParameter, Any?>()

            for (param in constructor.parameters) {
                val name = param.name
                val prop = clazz.members.find { it.name == name }
                val column = prop?.findAnnotation<Column>()

                if (column != null && name != null) {
                    val index = cursor.getColumnIndexOrThrow(column.name)
                    val value = when (param.type.classifier) {
                        Int::class -> cursor.getInt(index)
                        Long::class -> cursor.getLong(index)
                        Float::class -> cursor.getFloat(index)
                        Double::class -> cursor.getDouble(index)
                        String::class -> cursor.getString(index)
                        Boolean::class -> cursor.getInt(index) != 0
                        else -> null
                    }
                    argMap[param] = value
                }
            }

            val obj = params(constructor, argMap)
            result.add(obj)
        }

        cursor.close()
        return result
    }

    fun <T : Any> update(db: SQLiteDatabase, obj: T) {
        val clazz = obj::class
        val values = ContentValues()
        var whereClause = ""
        var whereArg: String? = null

        clazz.memberProperties.forEach { prop ->
            val col = prop.findAnnotation<Column>() ?: return@forEach
            val value = prop.call(obj)

            if (col.primaryKey) {
                whereClause = "${col.name} = ?"
                whereArg = value.toString()
            } else {
                when (value) {
                    is String -> values.put(col.name, value)
                    is Int -> values.put(col.name, value)
                    is Long -> values.put(col.name, value)
                    is Float -> values.put(col.name, value)
                    is Double -> values.put(col.name, value)
                    is Boolean -> values.put(col.name, if (value) 1 else 0)
                }
            }
        }

        db.update(OrmUtils.getTableName(clazz), values, whereClause, arrayOf(whereArg))
    }

    fun <T : Any> delete(db: SQLiteDatabase, obj: T) {
        val clazz = obj::class
        for (prop in clazz.memberProperties) {
            val col = prop.findAnnotation<Column>()
            if (col?.primaryKey == true) {
                val value = prop.call(obj)?.toString()
                if (value != null) {
                    db.delete(OrmUtils.getTableName(clazz), "${col.name} = ?", arrayOf(value))
                    return
                }
            }
        }
    }

    fun <T : Any> queryWhere(
        db: SQLiteDatabase,
        clazz: KClass<T>,
        where: String,
        args: Array<String>
    ): List<T> {
        val result = mutableListOf<T>()
        val table = OrmUtils.getTableName(clazz)
        val cursor = db.query(table, null, where, args, null, null, null)

        val constructor = clazz.primaryConstructor ?: throw IllegalArgumentException("Primary constructor required")

        while (cursor.moveToNext()) {
            val argMap = mutableMapOf<KParameter, Any?>()

            for (param in constructor.parameters) {
                val name = param.name ?: continue
                val prop = clazz.members.find { it.name == name } ?: continue
                val col = prop.findAnnotation<Column>() ?: continue
                val index = cursor.getColumnIndexOrThrow(col.name)
                val value = when (param.type.classifier) {
                    Int::class -> cursor.getInt(index)
                    Long::class -> cursor.getLong(index)
                    Float::class -> cursor.getFloat(index)
                    Double::class -> cursor.getDouble(index)
                    String::class -> cursor.getString(index)
                    Boolean::class -> cursor.getInt(index) != 0
                    else -> null
                }
                argMap[param] = value
            }

            val instance = constructor.callBy(argMap)
            result.add(instance)
        }

        cursor.close()
        return result
    }

}
