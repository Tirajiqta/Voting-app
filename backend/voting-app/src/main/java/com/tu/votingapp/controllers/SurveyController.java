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

@RestController
@RequestMapping("/api/surveys")
@Validated
@RequiredArgsConstructor
public class SurveyController {
    private final SurveyService surveyService;

    /** Create a new survey */
    @PostMapping
    @Validated(ValidationGroups.Create.class)
    public ResponseEntity<SurveyDTO> createSurvey(
            @Valid @RequestBody SurveyRequestDTO request) {
        SurveyDTO dto = surveyService.createSurvey(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    /** Update an existing survey */
    @PutMapping("/{id}")
    @Validated(ValidationGroups.Update.class)
    public ResponseEntity<SurveyDTO> updateSurvey(
            @PathVariable Long id,
            @Valid @RequestBody SurveyRequestDTO request) {
        request.setId(id);
        SurveyDTO dto = surveyService.updateSurvey(request);
        return ResponseEntity.ok(dto);
    }

    /** Delete a survey */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSurvey(@PathVariable Long id) {
        surveyService.deleteSurvey(id);
        return ResponseEntity.noContent().build();
    }

    /** Get survey details */
    @GetMapping("/{id}")
    public ResponseEntity<SurveyDTO> getSurvey(@PathVariable Long id) {
        SurveyDTO dto = surveyService.getSurveyById(id);
        return ResponseEntity.ok(dto);
    }

    /** List surveys with pagination and optional status filter */
    @GetMapping
    public ResponseEntity<PagedResponseDTO<SurveyDTO>> listSurveys(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) SurveyStatus status) {
        PagedResponseDTO<SurveyDTO> pageDto = surveyService.listSurveys(page, size, status);
        return ResponseEntity.ok(pageDto);
    }

    // --- Questions ---

    /** Add a question to a survey */
    @PostMapping("/{surveyId}/questions")
    @Validated(ValidationGroups.Create.class)
    public ResponseEntity<SurveyQuestionDTO> addQuestion(
            @PathVariable Long surveyId,
            @Valid @RequestBody SurveyQuestionRequestDTO request) {
        request.setSurveyId(surveyId);
        SurveyQuestionDTO dto = surveyService.addQuestion(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    /** Update a question */
    @PutMapping("/{surveyId}/questions/{questionId}")
    @Validated(ValidationGroups.Update.class)
    public ResponseEntity<SurveyQuestionDTO> updateQuestion(
            @PathVariable Long surveyId,
            @PathVariable Long questionId,
            @Valid @RequestBody SurveyQuestionRequestDTO request) {
        request.setId(questionId);
        request.setSurveyId(surveyId);
        SurveyQuestionDTO dto = surveyService.updateQuestion(request);
        return ResponseEntity.ok(dto);
    }

    /** Delete a question */
    @DeleteMapping("/{surveyId}/questions/{questionId}")
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable Long surveyId,
            @PathVariable Long questionId) {
        // surveyId path param is for clarity; service uses questionId only
        surveyService.deleteQuestion(questionId);
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
        request.setQuestionId(questionId);
        SurveyOptionDTO dto = surveyService.addOption(request);
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
        request.setId(optionId);
        request.setQuestionId(questionId);
        SurveyOptionDTO dto = surveyService.updateOption(request);
        return ResponseEntity.ok(dto);
    }

    /** Delete an option */
    @DeleteMapping("/{surveyId}/questions/{questionId}/options/{optionId}")
    public ResponseEntity<Void> deleteOption(
            @PathVariable Long surveyId,
            @PathVariable Long questionId,
            @PathVariable Long optionId) {
        surveyService.deleteOption(optionId);
        return ResponseEntity.noContent().build();
    }

    // --- Responses ---

    /** Submit a user's response to a survey question */
    @PostMapping("/{surveyId}/responses")
    public ResponseEntity<SurveyResponseDTO> submitResponse(
            @PathVariable Long surveyId,
            @Valid @RequestBody SurveyResponseRequestDTO request) {
        // ensure surveyId matches
        if (!Objects.equals(surveyId, request.getSurveyId())) {
            throw new IllegalArgumentException("Path surveyId must match request payload");
        }
        SurveyResponseDTO dto = surveyService.submitResponse(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    /** Get aggregated results for a survey */
    @GetMapping("/{surveyId}/results")
    public ResponseEntity<SurveyResultsDTO> getResults(@PathVariable Long surveyId) {
        SurveyResultsDTO dto = surveyService.getResults(surveyId);
        return ResponseEntity.ok(dto);
    }
}
