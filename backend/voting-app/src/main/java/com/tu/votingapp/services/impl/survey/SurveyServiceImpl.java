package com.tu.votingapp.services.impl.survey;

import com.tu.votingapp.dto.general.survey.SurveyDTO;
import com.tu.votingapp.dto.general.survey.SurveyOptionDTO;
import com.tu.votingapp.dto.general.survey.SurveyQuestionDTO;
import com.tu.votingapp.dto.general.survey.SurveyResponseDTO;
import com.tu.votingapp.dto.request.survey.SurveyOptionRequestDTO;
import com.tu.votingapp.dto.request.survey.SurveyQuestionRequestDTO;
import com.tu.votingapp.dto.request.survey.SurveyRequestDTO;
import com.tu.votingapp.dto.request.survey.SurveyResponseRequestDTO;
import com.tu.votingapp.dto.response.PagedResponseDTO;
import com.tu.votingapp.dto.response.survey.SurveyOptionResultsDTO;
import com.tu.votingapp.dto.response.survey.SurveyQuestionResultDTO;
import com.tu.votingapp.dto.response.survey.SurveyResultsDTO;
import com.tu.votingapp.entities.UserEntity;
import com.tu.votingapp.entities.surveys.SurveyEntity;
import com.tu.votingapp.entities.surveys.SurveyOptionEntity;
import com.tu.votingapp.entities.surveys.SurveyQuestionsEntity;
import com.tu.votingapp.entities.surveys.SurveyResponseEntity;
import com.tu.votingapp.enums.SurveyStatus;
import com.tu.votingapp.repositories.interfaces.UserRepository;
import com.tu.votingapp.repositories.interfaces.survey.SurveyOptionRepository;
import com.tu.votingapp.repositories.interfaces.survey.SurveyQuestionsRepository;
import com.tu.votingapp.repositories.interfaces.survey.SurveyRepository;
import com.tu.votingapp.repositories.interfaces.survey.SurveyResponseRepository;
import com.tu.votingapp.services.interfaces.SurveyService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SurveyServiceImpl implements SurveyService {
    private final SurveyRepository surveyRepository;
    private final SurveyQuestionsRepository questionRepository;
    private final SurveyOptionRepository optionRepository;
    private final SurveyResponseRepository responseRepository;
    private final UserRepository userRepository;
    private final Logger logger = Logger.getLogger(SurveyServiceImpl.class.getName());

    @Override
    @Transactional
    public SurveyDTO createSurvey(SurveyRequestDTO request) {
        logger.info(() -> "Creating survey: title='" + request.getTitle() + "', status='" + request.getStatus() + "'");
        LocalDate now = LocalDate.now();
        if (request.getStartDate().isBefore(now)) {
            logger.warning("startDate cannot be in the past");
            throw new IllegalArgumentException("startDate cannot be in the past");
        }
        if (!request.getEndDate().isAfter(request.getStartDate())) {
            logger.warning("endDate must be after startDate");
            throw new IllegalArgumentException("endDate must be after startDate");
        }
        if (!(request.getStatus() == SurveyStatus.DRAFT ||
                request.getStatus() == SurveyStatus.SCHEDULED)) {
            logger.warning(() -> "Invalid status at creation: " + request.getStatus());
            throw new IllegalArgumentException("status must be DRAFT or SCHEDULED");
        }
        SurveyEntity survey = new SurveyEntity();
        survey.setTitle(request.getTitle());
        survey.setDescription(request.getDescription());
        survey.setStartDate(java.sql.Date.valueOf(request.getStartDate()));
        survey.setEndDate(java.sql.Date.valueOf(request.getEndDate()));
        survey.setStatus(request.getStatus());
        UserEntity user = userRepository.getReferenceById(getCurrentUserId());
        survey.setCreatedBy(user);
        survey.setQuestions(List.of());
        SurveyEntity saved = surveyRepository.save(survey);
        logger.info(() -> "Survey created id=" + saved.getId());
        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public SurveyDTO updateSurvey(SurveyRequestDTO request) {
        logger.info(() -> "Updating survey id=" + request.getId());
        SurveyEntity existing = surveyRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Survey not found: " + request.getId()));
        LocalDate now = LocalDate.now();
        if (request.getStartDate() != null && request.getStartDate().isBefore(now)) {
            logger.warning("startDate cannot be in the past");
            throw new IllegalArgumentException("startDate cannot be in the past");
        }
        if (request.getEndDate() != null &&
                request.getStartDate() != null &&
                !request.getEndDate().isAfter(request.getStartDate())) {
            logger.warning("endDate must be after startDate");
            throw new IllegalArgumentException("endDate must be after startDate");
        }
        if (request.getTitle() != null) {
            logger.fine(() -> "Setting title to '" + request.getTitle() + "'");
            existing.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            logger.fine("Updating description");
            existing.setDescription(request.getDescription());
        }
        if (request.getStartDate() != null)
            existing.setStartDate(java.sql.Date.valueOf(request.getStartDate()));
        if (request.getEndDate() != null)
            existing.setEndDate(java.sql.Date.valueOf(request.getEndDate()));
        if (request.getStatus() != null) {
            logger.fine(() -> "Setting status to '" + request.getStatus() + "'");
            existing.setStatus(request.getStatus());
        }
        SurveyEntity saved = surveyRepository.save(existing);
        logger.info(() -> "Survey updated id=" + saved.getId());
        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public void deleteSurvey(Long id) {
        logger.info(() -> "Deleting survey id=" + id);
        SurveyEntity existing = surveyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Survey not found: " + id));
        if (existing.getStatus() != SurveyStatus.DRAFT) {
            logger.warning("Attempt to delete non-draft survey");
            throw new IllegalStateException("Can only delete surveys in DRAFT status");
        }
        surveyRepository.deleteById(id);
        logger.info(() -> "Survey deleted id=" + id);
    }

    @Override
    public SurveyDTO getSurveyById(Long id) {
        logger.info(() -> "Fetching survey id=" + id);
        SurveyEntity survey = surveyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Survey not found: " + id));
        logger.fine(() -> "Fetched survey: '" + survey.getTitle() + "'");
        return mapToDTO(survey);
    }

    @Override
    public PagedResponseDTO<SurveyDTO> listSurveys(int page, int size, SurveyStatus status) {
        logger.info(() -> String.format("Listing surveys page=%d size=%d status=%s", page, size, status));
        PageRequest pr = PageRequest.of(page, size);
        Page<SurveyEntity> pageData = (status != null)
                ? surveyRepository.findByStatus(status, pr)
                : surveyRepository.findAll(pr);
        List<SurveyDTO> content = pageData.getContent().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        logger.fine(() -> "Listed " + content.size() + " surveys");
        return new PagedResponseDTO<>(content, pageData.getNumber(), pageData.getSize(),
                pageData.getTotalElements(), pageData.getTotalPages(), pageData.isLast());
    }

    @Override
    @Transactional
    public SurveyQuestionDTO addQuestion(SurveyQuestionRequestDTO request) {
        logger.info(() -> "Adding question to survey id=" + request.getSurveyId());
        SurveyEntity survey = surveyRepository.findById(request.getSurveyId())
                .orElseThrow(() -> new RuntimeException("Survey not found: " + request.getSurveyId()));
        if (survey.getStatus() != SurveyStatus.DRAFT && survey.getStatus() != SurveyStatus.SCHEDULED) {
            logger.warning("Cannot add questions after survey is open");
            throw new IllegalStateException("Cannot add questions after survey is open");
        }
        SurveyQuestionsEntity q = new SurveyQuestionsEntity();
        q.setQuestionText(request.getQuestionText());
        q.setSurvey(survey);
        SurveyQuestionsEntity saved = questionRepository.save(q);
        logger.info(() -> "Question added id=" + saved.getId());
        return new SurveyQuestionDTO(saved.getId(), saved.getQuestionText(), Collections.emptyList());
    }

    @Override
    @Transactional
    public SurveyQuestionDTO updateQuestion(SurveyQuestionRequestDTO request) {
        logger.info(() -> "Updating question id=" + request.getId());
        SurveyQuestionsEntity q = questionRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Question not found: " + request.getId()));
        SurveyEntity survey = q.getSurvey();
        if (survey.getStatus() != SurveyStatus.DRAFT && survey.getStatus() != SurveyStatus.SCHEDULED) {
            logger.warning("Cannot update questions after survey is open");
            throw new IllegalStateException("Cannot update questions after survey is open");
        }
        q.setQuestionText(request.getQuestionText());
        SurveyQuestionsEntity saved = questionRepository.save(q);
        List<SurveyOptionDTO> opts = saved.getOptions().stream()
                .map(o -> new SurveyOptionDTO(o.getId(), o.getOptionText(), o.getVoteCount(), saved.getId()))
                .collect(Collectors.toList());
        logger.info(() -> "Question updated id=" + saved.getId());
        return new SurveyQuestionDTO(saved.getId(), saved.getQuestionText(), opts);
    }

    @Override
    @Transactional
    public void deleteQuestion(Long id) {
        logger.info(() -> "Deleting question id=" + id);
        SurveyQuestionsEntity q = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found: " + id));
        if (q.getSurvey().getStatus() != SurveyStatus.DRAFT && q.getSurvey().getStatus() != SurveyStatus.SCHEDULED) {
            logger.warning("Cannot delete questions after survey is open");
            throw new IllegalStateException("Cannot delete questions after survey is open");
        }
        questionRepository.deleteById(id);
        logger.info(() -> "Question deleted id=" + id);
    }

    @Override
    @Transactional
    public SurveyOptionDTO addOption(SurveyOptionRequestDTO request) {
        logger.info(() -> "Adding option to question id=" + request.getQuestionId());
        SurveyQuestionsEntity q = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Question not found: " + request.getQuestionId()));
        if (q.getSurvey().getStatus() != SurveyStatus.DRAFT && q.getSurvey().getStatus() != SurveyStatus.SCHEDULED) {
            logger.warning("Cannot add options after survey is open");
            throw new IllegalStateException("Cannot add options after survey is open");
        }
        SurveyOptionEntity opt = new SurveyOptionEntity();
        opt.setOptionText(request.getOptionText());
        opt.setQuestion(q);
        SurveyOptionEntity saved = optionRepository.save(opt);
        logger.info(() -> "Option added id=" + saved.getId());
        return new SurveyOptionDTO(saved.getId(), saved.getOptionText(), saved.getVoteCount(), q.getId());
    }

    @Override
    @Transactional
    public SurveyOptionDTO updateOption(SurveyOptionRequestDTO request) {
        logger.info(() -> "Updating option id=" + request.getId());
        SurveyOptionEntity opt = optionRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Option not found: " + request.getId()));
        if (opt.getQuestion().getSurvey().getStatus() != SurveyStatus.DRAFT &&
                opt.getQuestion().getSurvey().getStatus() != SurveyStatus.SCHEDULED) {
            logger.warning("Cannot update options after survey is open");
            throw new IllegalStateException("Cannot update options after survey is open");
        }
        opt.setOptionText(request.getOptionText());
        SurveyOptionEntity saved = optionRepository.save(opt);
        logger.info(() -> "Option updated id=" + saved.getId());
        return new SurveyOptionDTO(saved.getId(), saved.getOptionText(), saved.getVoteCount(), saved.getQuestion().getId());
    }

    @Override
    @Transactional
    public void deleteOption(Long id) {
        logger.info(() -> "Deleting option id=" + id);
        SurveyOptionEntity opt = optionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Option not found: " + id));
        if (opt.getQuestion().getSurvey().getStatus() != SurveyStatus.DRAFT &&
                opt.getQuestion().getSurvey().getStatus() != SurveyStatus.SCHEDULED) {
            logger.warning("Cannot delete options after survey is open");
            throw new IllegalStateException("Cannot delete options after survey is open");
        }
        optionRepository.deleteById(id);
        logger.info(() -> "Option deleted id=" + id);
    }

    @Override
    @Transactional
    public SurveyResponseDTO submitResponse(SurveyResponseRequestDTO request) {
        logger.info(() -> String.format("User %d submitting response for survey %d, question %d, option %d", getCurrentUserId(), request.getSurveyId(), request.getQuestionId(), request.getOptionId()));
        SurveyQuestionsEntity q = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Question not found: " + request.getQuestionId()));
        SurveyEntity survey = q.getSurvey();
        if (survey.getStatus() != SurveyStatus.OPEN) {
            logger.warning("Cannot respond to survey not OPEN");
            throw new IllegalStateException("Survey is not open for responses");
        }
        Long userId = getCurrentUserId();
        if (responseRepository.existsByUser_IdAndQuestion_Id(userId, q.getId())) {
            logger.warning("Duplicate survey response attempt");
            throw new IllegalStateException("User has already responded to this question");
        }
        SurveyOptionEntity opt = optionRepository.findById(request.getOptionId())
                .orElseThrow(() -> new RuntimeException("Option not found: " + request.getOptionId()));
        if (!opt.getQuestion().getId().equals(q.getId())) {
            logger.warning("Option does not belong to question");
            throw new IllegalArgumentException("Option does not belong to question");
        }
        opt.setVoteCount(opt.getVoteCount() + 1);
        optionRepository.save(opt);
        SurveyResponseEntity resp = new SurveyResponseEntity();
        resp.setUser(userRepository.findById(userId).orElseThrow());
        resp.setQuestion(q);
        resp.setOption(opt);
        resp.setRespondedAt(LocalDateTime.now());
        SurveyResponseEntity saved = responseRepository.save(resp);
        logger.info(() -> "Survey response recorded id=" + saved.getId());
        return new SurveyResponseDTO(saved.getId(), userId, survey.getId(), q.getId(), opt.getId(), saved.getRespondedAt());
    }

    @Override
    public SurveyResultsDTO getResults(Long surveyId) {
        logger.info(() -> "Fetching survey results for id=" + surveyId);
        SurveyEntity survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new RuntimeException("Survey not found: " + surveyId));
        List<SurveyQuestionResultDTO> questionResults =
                survey.getQuestions().stream()
                        .map(q -> {
                            List<SurveyOptionResultsDTO> opts = q.getOptions().stream()
                                    .map(o -> new SurveyOptionResultsDTO(o.getId(), o.getOptionText(), o.getVoteCount()))
                                    .collect(Collectors.toList());
                            return new SurveyQuestionResultDTO(q.getId(), q.getQuestionText(), opts);
                        })
                        .collect(Collectors.toList());
        logger.fine(() -> "Compiled results for " + questionResults.size() + " questions");
        return new SurveyResultsDTO(surveyId, questionResults);
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ((UserEntity) auth.getPrincipal()).getId();
    }

    private SurveyDTO mapToDTO(SurveyEntity e) {
        List<SurveyQuestionDTO> questions = e.getQuestions().stream().map(q -> {
            List<SurveyOptionDTO> opts = q.getOptions().stream()
                    .map(o -> new SurveyOptionDTO(o.getId(), o.getOptionText(), o.getVoteCount(), q.getId()))
                    .collect(Collectors.toList());
            return new SurveyQuestionDTO(q.getId(), q.getQuestionText(), opts);
        }).collect(Collectors.toList());
        logger.fine(() -> "Mapping survey entity to DTO id=" + e.getId());
        return new SurveyDTO(e.getId(), e.getTitle(), e.getDescription(),
                e.getStartDate(), e.getEndDate(), e.getStatus(), questions, e.getCreatedBy().getId());
    }
}