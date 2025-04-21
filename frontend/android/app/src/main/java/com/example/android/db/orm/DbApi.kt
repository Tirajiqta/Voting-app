package com.example.android.db.orm

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

object DbApi {

    private const val TAG = "DbApi" // Tag for logging

    // Note: This insert returns void. GenericDao will handle returning the ID.
    fun <T : Any> insert(db: SQLiteDatabase, obj: T) {
        val clazz = obj::class
        val tableName = OrmUtils.getTableName(clazz)
        val values = createContentValues(obj) // Extract value mapping

        if (values.size() > 0) {
            try {
                db.insertOrThrow(tableName, null, values)
            } catch (e: Exception) {
                Log.e(TAG, "Error inserting into $tableName: ${e.message}", e)
                // Optionally rethrow or handle differently
            }
        } else {
            Log.w(TAG, "No values to insert for object of class ${clazz.simpleName} into $tableName")
        }
    }

    // Note: This update returns void. GenericDao will handle returning rows affected.
    fun <T : Any> update(db: SQLiteDatabase, obj: T) {
        val clazz = obj::class
        val tableName = OrmUtils.getTableName(clazz)
        val values = ContentValues()
        var whereClause: String? = null
        var whereArg: String? = null

        clazz.memberProperties.forEach { prop ->
            val col = prop.findAnnotation<Column>() ?: return@forEach
            val value = prop.call(obj)

            if (col.primaryKey) {
                // Found the primary key for the WHERE clause
                if (whereClause != null) {
                    Log.w(TAG, "Multiple primary keys found for ${clazz.simpleName}. Using first one (${col.name}) for update WHERE clause.")
                } else {
                    whereClause = "${col.name} = ?"
                    whereArg = value?.toString() // Convert PK value to String for whereArgs
                }
            } else {
                // Add non-PK columns to ContentValues
                putValue(values, col.name, value)
            }
        }

        if (whereClause != null && whereArg != null && values.size() > 0) {
            try {
                db.update(tableName, values, whereClause, arrayOf(whereArg))
            } catch (e: Exception) {
                Log.e(TAG, "Error updating $tableName: ${e.message}", e)
            }
        } else if (whereClause == null || whereArg == null) {
            Log.e(TAG, "Cannot update ${clazz.simpleName} in $tableName: Primary key column not found or value is null.")
        } else {
            Log.w(TAG, "No non-primary key values to update for object of class ${clazz.simpleName} in $tableName")
        }
    }

    // Note: This delete returns void. GenericDao will handle returning rows affected.
    fun <T : Any> delete(db: SQLiteDatabase, obj: T) {
        val clazz = obj::class
        val tableName = OrmUtils.getTableName(clazz)
        var whereClause: String? = null
        var whereArg: String? = null

        // Find the primary key property and its value
        for (prop in clazz.memberProperties) {
            val col = prop.findAnnotation<Column>()
            if (col?.primaryKey == true) {
                whereClause = "${col.name} = ?"
                whereArg = prop.call(obj)?.toString() // Get PK value and convert to String
                break // Assume single primary key
            }
        }

        if (whereClause != null && whereArg != null) {
            try {
                db.delete(tableName, whereClause, arrayOf(whereArg))
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting from $tableName: ${e.message}", e)
            }
        } else {
            Log.e(TAG, "Cannot delete ${clazz.simpleName} from $tableName: Primary key column not found or value is null.")
        }
    }


    fun <T : Any> queryAll(db: SQLiteDatabase, clazz: KClass<T>): List<T> {
        return queryInternal(db, clazz, null, null)
    }

    fun <T : Any> queryWhere(
        db: SQLiteDatabase,
        clazz: KClass<T>,
        where: String,
        args: Array<String>
    ): List<T> {
        return queryInternal(db, clazz, where, args)
    }

    // --- Internal Helper Functions ---

    // Helper to create ContentValues from an object
    private fun <T : Any> createContentValues(obj: T): ContentValues {
        val clazz = obj::class
        val values = ContentValues()
        clazz.memberProperties.forEach { prop ->
            val col = prop.findAnnotation<Column>()
            // Include column if it's NOT a primary key that auto-increments
            // (because DB handles auto-increment values)
            if (col != null && !(col.primaryKey && col.autoIncrement)) {
                val value = prop.call(obj)
                putValue(values, col.name, value)
            }
        }
        return values
    }

    // Helper to put typed values into ContentValues, handling nulls
    private fun putValue(values: ContentValues, columnName: String, value: Any?) {
        when (value) {
            null -> values.putNull(columnName)
            is String -> values.put(columnName, value)
            is Int -> values.put(columnName, value)
            is Long -> values.put(columnName, value)
            is Float -> values.put(columnName, value)
            is Double -> values.put(columnName, value)
            is Boolean -> values.put(columnName, if (value) 1 else 0)
            is ByteArray -> values.put(columnName, value)
            else -> {
                Log.w(TAG, "Unsupported type for column '$columnName': ${value::class.simpleName}. Storing as String.")
                values.put(columnName, value.toString()) // Fallback: store as string
            }
        }
    }

    // Centralized query logic
    private fun <T : Any> queryInternal(
        db: SQLiteDatabase,
        clazz: KClass<T>,
        selection: String?,
        selectionArgs: Array<String>?
    ): List<T> {
        val result = mutableListOf<T>()
        val tableName = OrmUtils.getTableName(clazz)
        val constructor = clazz.primaryConstructor
            ?: throw IllegalArgumentException("Class ${clazz.simpleName} must have a primary constructor to be queried.")

        var cursor: Cursor? = null
        try {
            cursor = db.query(tableName, null, selection, selectionArgs, null, null, null)
            val columnCache = mutableMapOf<String, Int>() // Cache column indices

            while (cursor.moveToNext()) {
                try {
                    val instance = createInstanceFromCursor(cursor, clazz, constructor, columnCache)
                    result.add(instance)
                } catch (e: Exception) {
                    Log.e(TAG, "Error creating instance of ${clazz.simpleName} from cursor: ${e.message}", e)
                    // Skip this row or rethrow depending on desired behavior
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error querying table $tableName: ${e.message}", e)
            // Rethrow or return empty list
        } finally {
            cursor?.close() // Ensure cursor is always closed
        }
        return result
    }

    // Creates a single instance of T from the current Cursor position
    private fun <T : Any> createInstanceFromCursor(
        cursor: Cursor,
        clazz: KClass<T>,
        constructor: KFunction<T>,
        columnCache: MutableMap<String, Int> // Pass cache for efficiency
    ): T {
        val argMap = mutableMapOf<KParameter, Any?>()

        for (param in constructor.parameters) {
            val name = param.name ?: continue // Skip if parameter has no name
            // Find corresponding property (less efficient than caching, but simpler for now)
            val prop = clazz.memberProperties.find { it.name == name }
            val column = prop?.findAnnotation<Column>()

            if (column != null) {
                val columnName = column.name
                // Get column index from cache or cursor
                val index = columnCache.getOrPut(columnName) {
                    try {
                        cursor.getColumnIndexOrThrow(columnName)
                    } catch (e: IllegalArgumentException) {
                        Log.e(TAG, "Column '$columnName' not found in cursor for table ${OrmUtils.getTableName(clazz)}")
                        -1 // Indicate column not found
                    }
                }

                if (index != -1) { // Proceed only if column exists
                    // Check if the value is NULL in the database
                    val isDbNull = cursor.isNull(index)
                    val value: Any? = if (isDbNull) {
                        if (!param.type.isMarkedNullable) {
                            Log.w(TAG, "Database NULL found for non-nullable parameter '${param.name}' of type ${param.type} (Column: $columnName). Setting to default/null.")
                            // Decide default: null is okay if we let callBy handle it,
                            // otherwise provide a default based on type (0, false, "", etc.)
                            // Set to null and let callBy potentially fail if constructor requires non-null
                        }
                        null // Set to null if DB value is NULL
                    } else {
                        // Read non-null value based on parameter type
                        when (param.type.classifier) {
                            Int::class -> cursor.getInt(index)
                            Long::class -> cursor.getLong(index)
                            Float::class -> cursor.getFloat(index)
                            Double::class -> cursor.getDouble(index)
                            String::class -> cursor.getString(index)
                            Boolean::class -> cursor.getInt(index) != 0
                            ByteArray::class -> cursor.getBlob(index)
                            else -> {
                                Log.w(TAG,"Unsupported parameter type '${param.type}' for column '$columnName'. Reading as String.")
                                cursor.getString(index) // Fallback
                            }
                        }
                    }
                    argMap[param] = value
                } else {
                    // Column not found, what to do? Maybe set null if param allows?
                    if (param.type.isMarkedNullable) {
                        argMap[param] = null
                    } else {
                        throw IllegalStateException("Required column '$columnName' for non-nullable parameter '${param.name}' not found in query result for ${clazz.simpleName}.")
                    }
                }
            } else {
                // Parameter doesn't correspond to a @Column property
                // If the parameter is optional in the constructor, it's okay
                if (!param.isOptional) {
                    // If required, we might need to provide a default or throw error
                    Log.w(TAG, "Constructor parameter '${param.name}' for ${clazz.simpleName} does not correspond to a @Column annotated property and is not optional.")
                    // For simplicity, let callBy handle it, may fail if no default value exists
                }
            }
        }

        try {
            return constructor.callBy(argMap)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to call constructor for ${clazz.simpleName} with args: $argMap", e)
            throw IllegalArgumentException("Failed to instantiate ${clazz.simpleName}: ${e.message}", e)
        }
    }
}