package com.example.android.db

import android.content.Context
//import com.example.android.dao.GenericDao
// --- Import ALL your entity classes ---
import com.example.android.entity.election.*
// --------------------------------------
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.android.dao.election.CandidateDao
import com.example.android.dao.election.ElectionDao
import com.example.android.dao.election.PartyDao
import com.example.android.dao.election.PartyVoteDao
import com.example.android.dao.election.VoteDao

@Database(
    entities = [
        ElectionEntity::class,
        CandidateEntity::class,
        PartyEntity::class,
        PartyVoteEntity::class,
        VoteEntity::class
        // Add any other entities used by these DAOs
    ],
    version = 1, // Increment version on schema changes
    exportSchema = false // Or true if you want to export schema
)
abstract class AppDatabase : RoomDatabase() {

    // Abstract methods for Room to provide DAO implementations
    abstract fun electionDao(): ElectionDao
    abstract fun candidateDao(): CandidateDao
    abstract fun partyDao(): PartyDao
    abstract fun partyVoteDao(): PartyVoteDao
    abstract fun voteDao(): VoteDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "voting_app_database" // Choose a database name
                )
                    // Add migrations here if needed for production apps
                    .fallbackToDestructiveMigration() // Simple strategy for development
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}