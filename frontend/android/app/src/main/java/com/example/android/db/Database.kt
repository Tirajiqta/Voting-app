package com.example.android.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log // Import Log
import com.example.android.dao.GenericDao
import com.example.android.db.orm.OrmUtils
import com.example.android.db.orm.TableBuilder
// --- Import ALL your entity classes ---
import com.example.android.entity.* // Use wildcard or list all
import com.example.android.entity.election.*
import com.example.android.entity.referendum.*
import com.example.android.entity.survey.*
// --------------------------------------
import kotlin.reflect.KClass
import androidx.core.database.sqlite.transaction

class Database private constructor(context: Context) : SQLiteOpenHelper(
    context, DB_NAME, null, DB_VERSION
) {
    private val TAG = "DatabaseHelper" // Tag for logging

    companion object {
        private const val DB_NAME = "app.db"
        private const val DB_VERSION = 1 // Increment this when schema changes

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

    // Define the list of entity classes to create tables for
    private val entityClasses: List<KClass<*>> = listOf(
        RegionEntity::class,
        MunicipalityEntity::class,
        LocationEntity::class,
        LocationRegionEntity::class,
        RoleEntity::class,
        PermissionEntity::class,
        UserEntity::class,
        DocumentEntity::class,
        ElectionEntity::class,
        CandidateEntity::class,
        PartyEntity::class,
        PartyVoteEntity::class,
        VoteEntity::class,
        ReferendumEntity::class,
        ReferendumOptionEntity::class,
        ReferendumVoteEntity::class,
        SurveyEntity::class,
        SurveyQuestionEntity::class,
        SurveyOptionEntity::class,
        SurveyResponseEntity::class,
        RolePermissionEntity::class
        // Add any other entities used in your app
    )

    override fun onCreate(db: SQLiteDatabase) {
        Log.i(TAG, "Creating database tables...")
        db.transaction() { // Use transaction for atomic creation
            try {
                entityClasses.forEach { entity ->
                    try {
                        val createSQL = TableBuilder.createTableSQL(entity)
                        Log.d(TAG, "Executing SQL: $createSQL") // Log the SQL
                        execSQL(createSQL)
                    } catch (e: Exception) {
                        // Log specific error for the entity that failed
                        Log.e(
                            TAG,
                            "Failed to create table for ${entity.simpleName}: ${e.message}",
                            e
                        )
                        // Rethrow to potentially stop DB creation if one table fails
                        throw e
                    }
                }
                Log.i(TAG, "Database tables created successfully.")
            } catch (e: Exception) {
                // Catch errors from createTableSQL itself (e.g., no columns) or rethrown execSQL errors
                Log.e(TAG, "Error during database table creation: ${e.message}", e)
                // Handle the failure (e.g., maybe the app can't run without the DB)
            } finally {
            }
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.w(TAG, "Upgrading database from version $oldVersion to $newVersion.")
        // WARNING: This basic implementation drops all tables and loses data.
        // Implement proper migration strategies for production apps.
        db.beginTransaction()
        try {
            entityClasses.reversed().forEach { entity -> // Drop in reverse order (helps with FKs if added later)
                try {
                    val tableName = OrmUtils.getTableName(entity)
                    Log.d(TAG, "Dropping table $tableName")
                    db.execSQL("DROP TABLE IF EXISTS $tableName")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to drop table for ${entity.simpleName}: ${e.message}", e)
                }
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        // Recreate tables
        onCreate(db)
    }

    // Expose DAOs (Keep your existing getDao method)
    fun <T : Any> getDao(entityClass: KClass<T>): GenericDao<T> {
        // Ensures writableDatabase is called, which triggers onCreate/onUpgrade if needed
        return GenericDao(writableDatabase, entityClass)
    }
}