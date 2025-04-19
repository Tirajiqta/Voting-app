package com.example.android.repository

import com.example.android.dao.survey.*
import com.example.android.entity.survey.*

class SurveyRepository(
    private val surveyDao: SurveyDao,
    private val questionDao: SurveyQuestionDao,
    private val optionDao: SurveyOptionDao,
    private val responseDao: SurveyResponseDao
) {

    // --- Survey ---
    fun insertSurvey(survey: SurveyEntity) = surveyDao.insert(survey)
    fun updateSurvey(survey: SurveyEntity) = surveyDao.update(survey)
    fun deleteSurvey(survey: SurveyEntity) = surveyDao.delete(survey)
    fun getAllSurveys(): List<SurveyEntity> = surveyDao.getAll()
    fun getSurveyById(id: Long): SurveyEntity? = surveyDao.getById(id)

    // --- Survey Questions ---
    fun insertQuestion(question: SurveyQuestionEntity) = questionDao.insert(question)
    fun updateQuestion(question: SurveyQuestionEntity) = questionDao.update(question)
    fun deleteQuestion(question: SurveyQuestionEntity) = questionDao.delete(question)
    fun getAllQuestions(): List<SurveyQuestionEntity> = questionDao.getAll()
    fun getQuestionById(id: Long): SurveyQuestionEntity? = questionDao.getById(id)

    // --- Survey Options ---
    fun insertOption(option: SurveyOptionEntity) = optionDao.insert(option)
    fun updateOption(option: SurveyOptionEntity) = optionDao.update(option)
    fun deleteOption(option: SurveyOptionEntity) = optionDao.delete(option)
    fun getAllOptions(): List<SurveyOptionEntity> = optionDao.getAll()
    fun getOptionById(id: Long): SurveyOptionEntity? = optionDao.getById(id)

    // --- Survey Responses ---
    fun insertResponse(response: SurveyResponseEntity) = responseDao.insert(response)
    fun updateResponse(response: SurveyResponseEntity) = responseDao.update(response)
    fun deleteResponse(response: SurveyResponseEntity) = responseDao.delete(response)
    fun getAllResponses(): List<SurveyResponseEntity> = responseDao.getAll()
    fun getResponseById(id: Long): SurveyResponseEntity? = responseDao.getById(id)
}