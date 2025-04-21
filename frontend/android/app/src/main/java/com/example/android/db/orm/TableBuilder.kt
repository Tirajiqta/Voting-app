package com.example.android.db.orm

import android.util.Log
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

object TableBuilder {

    private const val TAG = "com.example.android.db.orm.TableBuilder" // Tag for logging

    /**
     * Generates a CREATE TABLE SQL statement based on a Kotlin class and @Column annotations.
     * Includes PRIMARY KEY, AUTOINCREMENT (for INTEGER PKs), and NOT NULL constraints.
     */
    fun createTableSQL(clazz: KClass<*>): String {
        val tableName = OrmUtils.getTableName(clazz) // Use OrmUtils for consistency
        val columns = mutableListOf<String>()

        clazz.memberProperties.forEach { prop ->
            val colAnnotation = prop.findAnnotation<Column>()
            if (colAnnotation != null) {
                val columnName = colAnnotation.name
                if (columnName.isBlank()) {
                    Log.e(TAG, "Column name cannot be blank for property ${prop.name} in class $tableName")
                    // Optionally throw an exception or skip this column
                    return@forEach // Skip this invalid column
                }

                val sqlType = getSQLType(prop.returnType)
                val columnDefinition = StringBuilder("$columnName $sqlType")

                if (colAnnotation.primaryKey) {
                    columnDefinition.append(" PRIMARY KEY")
                    // Add AUTOINCREMENT only if primary key and type is INTEGER
                    if (colAnnotation.autoIncrement && sqlType == "INTEGER") {
                        columnDefinition.append(" AUTOINCREMENT")
                    } else if (colAnnotation.autoIncrement) {
                        Log.w(TAG, "AUTOINCREMENT specified but PRIMARY KEY '$columnName' in table '$tableName' is not INTEGER type ($sqlType). Ignoring AUTOINCREMENT.")
                    }
                }

                // Add NOT NULL constraint if the Kotlin property is not nullable AND it's not an auto-incrementing PK
                // (Auto-increment PKs are implicitly NOT NULL and handled by SQLite during insert)
                if (!prop.returnType.isMarkedNullable && !(colAnnotation.primaryKey && colAnnotation.autoIncrement)) {
                    columnDefinition.append(" NOT NULL")
                }

                columns.add(columnDefinition.toString())
            }
        }

        if (columns.isEmpty()) {
            throw IllegalStateException("No properties annotated with @Column found in class ${clazz.simpleName}. Cannot create table '$tableName'.")
        }

        // Use joinToString for cleaner assembly
        val columnsSql = columns.joinToString(", ")
        return "CREATE TABLE IF NOT EXISTS $tableName ($columnsSql);"
    }

    // --- Other utility functions (keep as they were) ---

    fun truncateTableSQL(clazz: KClass<*>): String =
        "DELETE FROM ${OrmUtils.getTableName(clazz)};" // Use OrmUtils

    fun addColumnSQL(clazz: KClass<*>, name: String, type: KType, notNull: Boolean = false, defaultVal: String? = null): String {
        val tableName = OrmUtils.getTableName(clazz) // Use OrmUtils
        val typeStr = getSQLType(type)
        val notNullPart = if (notNull) " NOT NULL" else ""
        val defaultPart = defaultVal?.let { " DEFAULT '$it'" } ?: "" // Add quotes for default text values
        return "ALTER TABLE $tableName ADD COLUMN $name $typeStr$notNullPart$defaultPart;"
    }

    // Updated getSQLType to include ByteArray
    private fun getSQLType(type: KType): String = when (type.classifier as? KClass<*>) {
        Int::class -> "INTEGER"
        Long::class -> "INTEGER" // SQLite uses INTEGER for various sizes
        Float::class -> "REAL"
        Double::class -> "REAL"
        String::class -> "TEXT"
        Boolean::class -> "INTEGER" // Store Booleans as 0 or 1
        ByteArray::class -> "BLOB" // Added support for BLOB
        else -> {
            Log.w(TAG, "Unsupported type '$type' encountered. Defaulting to TEXT.")
            "TEXT" // Default to TEXT if type is unknown or complex
        }
    }
}