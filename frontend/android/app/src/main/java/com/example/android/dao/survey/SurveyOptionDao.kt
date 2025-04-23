package com.example.android.dao.survey

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.android.entity.survey.SurveyOptionEntity

@Dao
interface SurveyOptionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(surveyOption: SurveyOptionEntity)

    @Query("SELECT * FROM survey_options WHERE question_id = :questionId")
    fun getOptionsByQuestionId(questionId: Long): List<SurveyOptionEntity>

    @Query("SELECT * FROM survey_options")
    fun getAll(): List<SurveyOptionEntity>

    @Query("DELETE FROM survey_options")
    fun deleteAll()

    @Query("DELETE FROM survey_options WHERE question_id = :questionId")
    fun deleteByQuestionId(questionId: Long)

}