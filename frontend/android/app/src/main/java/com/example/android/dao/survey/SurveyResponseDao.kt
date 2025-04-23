package com.example.android.dao.survey

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.android.entity.survey.SurveyResponseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SurveyResponseDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertResponse(response: SurveyResponseEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllResponses(responses: List<SurveyResponseEntity>)
    
    @Delete
    fun deleteResponse(response: SurveyResponseEntity)

    @Query("DELETE FROM survey_responses WHERE id = :responseId")
    fun deleteResponseById(responseId: Long): Int
    
    @Query("SELECT * FROM survey_responses WHERE id = :responseId")
    fun getResponseById(responseId: Long): SurveyResponseEntity?
    
    @Query("SELECT * FROM survey_responses WHERE user_id = :userId AND question_id = :questionId LIMIT 1")
    fun getResponseByUserAndQuestion(userId: Long, questionId: Long): SurveyResponseEntity?
    
    @Query("SELECT * FROM survey_responses WHERE user_id = :userId ORDER BY responded_at DESC")
    fun getResponsesByUserIdFlow(userId: Long): Flow<List<SurveyResponseEntity>>

    @Query("SELECT * FROM survey_responses WHERE user_id = :userId ORDER BY responded_at DESC")
    fun getResponsesByUserIdList(userId: Long): List<SurveyResponseEntity>

    @Query("SELECT * FROM survey_responses WHERE question_id = :questionId ORDER BY responded_at DESC")
    fun getResponsesByQuestionIdFlow(questionId: Long): Flow<List<SurveyResponseEntity>>


    @Query("SELECT * FROM survey_responses WHERE question_id = :questionId ORDER BY responded_at DESC")
    fun getResponsesByQuestionIdList(questionId: Long): List<SurveyResponseEntity>


    @Query("SELECT * FROM survey_responses ORDER BY responded_at DESC")
    fun getAllResponsesFlow(): Flow<List<SurveyResponseEntity>>
    
    @Query("SELECT * FROM survey_responses ORDER BY responded_at DESC")
    fun getAllResponsesList(): List<SurveyResponseEntity>
    
    @Query("DELETE FROM survey_responses")
    fun deleteAllResponses()
    
    @Query("DELETE FROM survey_responses WHERE user_id = :userId")
    fun deleteResponsesByUser(userId: Long): Int

}