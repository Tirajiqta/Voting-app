package com.example.android.db.orm

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.findAnnotation
object TableBuilder {

    fun createTableSQL(clazz: KClass<*>): String {
        val sb = StringBuilder("CREATE TABLE IF NOT EXISTS ${clazz.simpleName} (")

        clazz.memberProperties.forEach { prop ->
            val col = prop.findAnnotation<Column>()
            if (col != null) {
                sb.append("${col.name} ${getSQLType(prop.returnType)}")
                if (col.primaryKey) {
                    sb.append(" PRIMARY KEY")
                    if (col.autoIncrement) sb.append(" AUTOINCREMENT")
                }
                sb.append(", ")
            }
        }

        sb.setLength(sb.length - 2)
        sb.append(");")
        return sb.toString()
    }

    fun truncateTableSQL(clazz: KClass<*>): String =
        "DELETE FROM ${clazz.simpleName};"

    fun addColumnSQL(clazz: KClass<*>, name: String, type: KType, notNull: Boolean = false, defaultVal: String? = null): String {
        val typeStr = getSQLType(type)
        val notNullPart = if (notNull) " NOT NULL" else ""
        val defaultPart = defaultVal?.let { " DEFAULT $it" } ?: ""
        return "ALTER TABLE ${clazz.simpleName} ADD COLUMN $name $typeStr$notNullPart$defaultPart;"
    }

    private fun getSQLType(type: KType): String = when (type.classifier) {
        Int::class, Long::class -> "INTEGER"
        Float::class, Double::class -> "REAL"
        String::class -> "TEXT"
        Boolean::class -> "INTEGER"
        else -> "TEXT"
    }
}
