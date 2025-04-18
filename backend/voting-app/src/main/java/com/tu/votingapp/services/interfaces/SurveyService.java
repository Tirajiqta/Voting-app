package com.tu.votingapp.services.interfaces;

import com.tu.votingapp.dto.general.survey.SurveyDTO;
import com.tu.votingapp.dto.general.survey.SurveyOptionDTO;
import com.tu.votingapp.dto.general.survey.SurveyQuestionDTO;
import com.tu.votingapp.dto.general.survey.SurveyResponseDTO;
import com.tu.votingapp.dto.request.survey.SurveyOptionRequestDTO;
import com.tu.votingapp.dto.request.survey.SurveyQuestionRequestDTO;
import com.tu.votingapp.dto.request.survey.SurveyRequestDTO;
import com.tu.votingapp.dto.request.survey.SurveyResponseRequestDTO;
import com.tu.votingapp.dto.response.PagedResponseDTO;
import com.tu.votingapp.dto.response.survey.SurveyResultsDTO;
import com.tu.votingapp.enums.SurveyStatus;

public interface SurveyService {
    SurveyDTO createSurvey(SurveyRequestDTO request);
    SurveyDTO updateSurvey(SurveyRequestDTO request);
    void deleteSurvey(Long id);
    SurveyDTO getSurveyById(Long id);
    PagedResponseDTO<SurveyDTO> listSurveys(int page, int size, SurveyStatus status);

    SurveyQuestionDTO addQuestion(SurveyQuestionRequestDTO request);
    SurveyQuestionDTO updateQuestion(SurveyQuestionRequestDTO request);
    void deleteQuestion(Long id);

    SurveyOptionDTO addOption(SurveyOptionRequestDTO request);
    SurveyOptionDTO updateOption(SurveyOptionRequestDTO request);
    void deleteOption(Long id);

    SurveyResponseDTO submitResponse(SurveyResponseRequestDTO request);
    SurveyResultsDTO getResults(Long surveyId);
}
