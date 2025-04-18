package com.tu.votingapp.controllers;

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
import com.tu.votingapp.services.interfaces.SurveyService;
import com.tu.votingapp.validation.ValidationGroups;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/surveys")
@Validated
@RequiredArgsConstructor
public class SurveyController {
    private final SurveyService surveyService;
    private final Logger logger = Logger.getLogger(SurveyController.class.getName());

    /** Create a new survey */
    @PostMapping
    @Validated(ValidationGroups.Create.class)
    public ResponseEntity<SurveyDTO> createSurvey(
            @Valid @RequestBody SurveyRequestDTO request) {
        logger.info(() -> "Creating survey: title='" + request.getTitle() + "'");
        SurveyDTO dto = surveyService.createSurvey(request);
        logger.info(() -> "Created survey id=" + dto.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    /** Update an existing survey */
    @PutMapping("/{id}")
    @Validated(ValidationGroups.Update.class)
    public ResponseEntity<SurveyDTO> updateSurvey(
            @PathVariable Long id,
            @Valid @RequestBody SurveyRequestDTO request) {
        logger.info(() -> "Updating survey id=" + id);
        request.setId(id);
        SurveyDTO dto = surveyService.updateSurvey(request);
        logger.info(() -> "Updated survey id=" + dto.getId());
        return ResponseEntity.ok(dto);
    }

    /** Delete a survey */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSurvey(@PathVariable Long id) {
        logger.info(() -> "Deleting survey id=" + id);
        surveyService.deleteSurvey(id);
        logger.info(() -> "Deleted survey id=" + id);
        return ResponseEntity.noContent().build();
    }

    /** Get survey details */
    @GetMapping("/{id}")
    public ResponseEntity<SurveyDTO> getSurvey(@PathVariable Long id) {
        logger.info(() -> "Fetching survey id=" + id);
        SurveyDTO dto = surveyService.getSurveyById(id);
        logger.fine(() -> "Fetched survey: title='" + dto.getTitle() + "'");
        return ResponseEntity.ok(dto);
    }

    /** List surveys with pagination and optional status filter */
    @GetMapping
    public ResponseEntity<PagedResponseDTO<SurveyDTO>> listSurveys(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) SurveyStatus status) {
        logger.info(() -> String.format(
                "Listing surveys: page=%d, size=%d, status=%s", page, size, status));
        PagedResponseDTO<SurveyDTO> pageDto = surveyService.listSurveys(page, size, status);
        logger.fine(() -> String.format(
                "Listed %d surveys on page %d", pageDto.getContent().size(), pageDto.getPage()));
        return ResponseEntity.ok(pageDto);
    }

    // --- Questions ---

    /** Add a question to a survey */
    @PostMapping("/{surveyId}/questions")
    @Validated(ValidationGroups.Create.class)
    public ResponseEntity<SurveyQuestionDTO> addQuestion(
            @PathVariable Long surveyId,
            @Valid @RequestBody SurveyQuestionRequestDTO request) {
        logger.info(() -> "Adding question to survey id=" + surveyId);
        request.setSurveyId(surveyId);
        SurveyQuestionDTO dto = surveyService.addQuestion(request);
        logger.info(() -> "Added question id=" + dto.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    /** Update a question */
    @PutMapping("/{surveyId}/questions/{questionId}")
    @Validated(ValidationGroups.Update.class)
    public ResponseEntity<SurveyQuestionDTO> updateQuestion(
            @PathVariable Long surveyId,
            @PathVariable Long questionId,
            @Valid @RequestBody SurveyQuestionRequestDTO request) {
        logger.info(() -> "Updating question id=" + questionId + " in survey id=" + surveyId);
        request.setId(questionId);
        request.setSurveyId(surveyId);
        SurveyQuestionDTO dto = surveyService.updateQuestion(request);
        logger.info(() -> "Updated question id=" + dto.getId());
        return ResponseEntity.ok(dto);
    }

    /** Delete a question */
    @DeleteMapping("/{surveyId}/questions/{questionId}")
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable Long surveyId,
            @PathVariable Long questionId) {
        logger.info(() -> "Deleting question id=" + questionId + " from survey id=" + surveyId);
        surveyService.deleteQuestion(questionId);
        logger.info(() -> "Deleted question id=" + questionId);
        return ResponseEntity.noContent().build();
    }

    // --- Options ---

    /** Add an option to a question */
    @PostMapping("/{surveyId}/questions/{questionId}/options")
    @Validated(ValidationGroups.Create.class)
    public ResponseEntity<SurveyOptionDTO> addOption(
            @PathVariable Long surveyId,
            @PathVariable Long questionId,
            @Valid @RequestBody SurveyOptionRequestDTO request) {
        logger.info(() -> "Adding option to question id=" + questionId + " in survey id=" + surveyId);
        request.setQuestionId(questionId);
        SurveyOptionDTO dto = surveyService.addOption(request);
        logger.info(() -> "Added option id=" + dto.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    /** Update an option */
    @PutMapping("/{surveyId}/questions/{questionId}/options/{optionId}")
    @Validated(ValidationGroups.Update.class)
    public ResponseEntity<SurveyOptionDTO> updateOption(
            @PathVariable Long surveyId,
            @PathVariable Long questionId,
            @PathVariable Long optionId,
            @Valid @RequestBody SurveyOptionRequestDTO request) {
        logger.info(() -> "Updating option id=" + optionId + " for question id=" + questionId);
        request.setId(optionId);
        request.setQuestionId(questionId);
        SurveyOptionDTO dto = surveyService.updateOption(request);
        logger.info(() -> "Updated option id=" + dto.getId());
        return ResponseEntity.ok(dto);
    }

    /** Delete an option */
    @DeleteMapping("/{surveyId}/questions/{questionId}/options/{optionId}")
    public ResponseEntity<Void> deleteOption(
            @PathVariable Long surveyId,
            @PathVariable Long questionId,
            @PathVariable Long optionId) {
        logger.info(() -> "Deleting option id=" + optionId + " from question id=" + questionId);
        surveyService.deleteOption(optionId);
        logger.info(() -> "Deleted option id=" + optionId);
        return ResponseEntity.noContent().build();
    }

    // --- Responses ---

    /** Submit a user's response to a survey question */
    @PostMapping("/{surveyId}/responses")
    public ResponseEntity<SurveyResponseDTO> submitResponse(
            @PathVariable Long surveyId,
            @Valid @RequestBody SurveyResponseRequestDTO request) {
        logger.info(() -> "Submitting response for survey id=" + surveyId + " question id=" + request.getQuestionId());
        if (!Objects.equals(surveyId, request.getSurveyId())) {
            throw new IllegalArgumentException("Path surveyId must match request payload");
        }
        SurveyResponseDTO dto = surveyService.submitResponse(request);
        logger.info(() -> "Submitted response id=" + dto.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    /** Get aggregated results for a survey */
    @GetMapping("/{surveyId}/results")
    public ResponseEntity<SurveyResultsDTO> getResults(@PathVariable Long surveyId) {
        logger.info(() -> "Fetching results for survey id=" + surveyId);
        SurveyResultsDTO dto = surveyService.getResults(surveyId);
        logger.fine(() -> String.format("Results fetched: %d questions", dto.getQuestions().size()));
        return ResponseEntity.ok(dto);
    }
}