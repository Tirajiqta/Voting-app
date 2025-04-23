package com.example.android.dao.survey

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.android.entity.survey.SurveyQuestionEntity

@Dao
interface SurveyQuestionDao {
    @Insert
    fun insert(surveyQuestion: SurveyQuestionEntity)

    @Query("SELECT * FROM survey_questions WHERE survey_id = :surveyId")
    fun getSurveyQuestions(surveyId: Long): List<SurveyQuestionEntity>

    @Query("DELETE FROM survey_questions WHERE survey_id = :surveyId")
    fun deleteSurveyQuestions(surveyId: Long)

    @Query("DELETE FROM survey_questions")
    fun deleteAllSurveyQuestions()

    @Query("SELECT * FROM survey_questions")
    fun getAllSurveyQuestions(): List<SurveyQuestionEntity>

    @Query("SELECT * FROM survey_questions WHERE id = :id")
    fun getSurveyQuestionById(id: Long): SurveyQuestionEntity?

    @Query("SELECT * FROM survey_questions WHERE question_text = :text")
    fun getSurveyQuestionByText(text: String): SurveyQuestionEntity?

    @Query("SELECT * FROM survey_questions WHERE survey_id = :surveyId AND question_text = :text")
    fun getSurveyQuestionBySurveyIdAndText(surveyId: Long, text: String): SurveyQuestionEntity?

}