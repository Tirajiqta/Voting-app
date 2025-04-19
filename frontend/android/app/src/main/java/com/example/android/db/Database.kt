package com.example.android.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.android.dao.GenericDao
import com.example.android.db.orm.TableBuilder
import com.example.android.entity.DocumentEntity
import com.example.android.entity.LocationEntity
import com.example.android.entity.LocationRegionEntity
import com.example.android.entity.MunicipalityEntity
import com.example.android.entity.PermissionEntity
import com.example.android.entity.RegionEntity
import com.example.android.entity.RoleEntity
import com.example.android.entity.RolePermissionEntity
import com.example.android.entity.UserEntity
import com.example.android.entity.election.CandidateEntity
import com.example.android.entity.election.ElectionEntity
import com.example.android.entity.election.PartyEntity
import com.example.android.entity.election.PartyVoteEntity
import com.example.android.entity.election.VoteEntity
import com.example.android.entity.referendum.ReferendumEntity
import com.example.android.entity.referendum.ReferendumOptionEntity
import com.example.android.entity.referendum.ReferendumVoteEntity
import com.example.android.entity.survey.SurveyEntity
import com.example.android.entity.survey.SurveyOptionEntity
import com.example.android.entity.survey.SurveyQuestionEntity
import com.example.android.entity.survey.SurveyResponseEntity
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