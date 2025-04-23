package com.example.android.dao.survey

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.android.entity.survey.SurveyEntity

@Dao
interface SurveyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(survey: SurveyEntity)

    @Update
    fun update(survey: SurveyEntity)

    @Delete
    fun delete(survey: SurveyEntity)
    @Query("SELECT * FROM surveys")
    fun getAll(): List<SurveyEntity>
    @Query("SELECT * FROM surveys WHERE id = :id")
    fun getById(id: Long): SurveyEntity?

    @Query("SELECT * FROM surveys WHERE created_by = :userId")
    fun getByUserId(userId: Long): List<SurveyEntity>

}