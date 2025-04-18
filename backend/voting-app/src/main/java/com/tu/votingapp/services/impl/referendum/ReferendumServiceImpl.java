package com.tu.votingapp.services.impl.referendum;

import com.tu.votingapp.dto.request.referendum.ReferendumRequestDTO;
import com.tu.votingapp.dto.request.referendum.ReferendumVoteRequestDTO;
import com.tu.votingapp.dto.response.PagedResponseDTO;
import com.tu.votingapp.dto.response.referendum.*;
import com.tu.votingapp.entities.referendum.ReferendumEntity;
import com.tu.votingapp.entities.referendum.ReferendumOptionEntity;
import com.tu.votingapp.entities.referendum.ReferendumVoteEntity;
import com.tu.votingapp.enums.ReferendumStatus;
import com.tu.votingapp.repositories.interfaces.UserRepository;
import com.tu.votingapp.repositories.interfaces.referendum.ReferendumOptionRepository;
import com.tu.votingapp.repositories.interfaces.referendum.ReferendumRepository;
import com.tu.votingapp.repositories.interfaces.referendum.ReferendumVoteRepository;
import com.tu.votingapp.services.interfaces.referendum.ReferendumService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReferendumServiceImpl implements ReferendumService {
    private final ReferendumRepository referendumRepository;
    private final ReferendumOptionRepository optionRepository;
    private final ReferendumVoteRepository voteRepository;
    private final UserRepository userRepository;
    private final Logger logger = Logger.getLogger(ReferendumServiceImpl.class.getName());

    @Override
    @Transactional
    public ReferendumResponseDTO createReferendum(ReferendumRequestDTO request) {
        logger.info(() -> "Creating referendum: title='" + request.getTitle() + "', status='" + request.getStatus() + "'");
        LocalDate now = LocalDate.now();
        if (request.getStartDate().isBefore(now)) {
            logger.warning("startDate cannot be in the past");
            throw new IllegalArgumentException("startDate cannot be in the past");
        }
        if (!request.getEndDate().isAfter(request.getStartDate())) {
            logger.warning("endDate must be after startDate");
            throw new IllegalArgumentException("endDate must be after startDate");
        }
        if (!(request.getStatus() == ReferendumStatus.DRAFT ||
                request.getStatus() == ReferendumStatus.SCHEDULED)) {
            logger.warning(() -> "Invalid status at creation: " + request.getStatus());
            throw new IllegalArgumentException("status must be DRAFT or SCHEDULED");
        }
        ReferendumEntity entity = new ReferendumEntity();
        entity.setTitle(request.getTitle());
        entity.setDescription(request.getDescription());
        entity.setQuestion(request.getQuestion());
        entity.setStartDate(java.sql.Date.valueOf(request.getStartDate()));
        entity.setEndDate(java.sql.Date.valueOf(request.getEndDate()));
        entity.setStatus(request.getStatus());
        entity.setCreatedBy(userRepository.getReferenceById(getCurrentUserId()));
        entity.setOptions(List.of());
        ReferendumEntity saved = referendumRepository.save(entity);
        logger.info(() -> "Referendum created id=" + saved.getId());
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public ReferendumResponseDTO updateReferendum(ReferendumRequestDTO request) {
        logger.info(() -> "Updating referendum id=" + request.getId());
        ReferendumEntity existing = referendumRepository.findById(request.getId())
                .orElseThrow(() -> {
                    logger.warning(() -> "Referendum not found: " + request.getId());
                    return new RuntimeException("Referendum not found");
                });
        LocalDate now = LocalDate.now();
        if (request.getStartDate() != null && request.getStartDate().isBefore(now)) {
            logger.warning("startDate cannot be in the past");
            throw new IllegalArgumentException("startDate cannot be in the past");
        }
        if (request.getEndDate() != null && request.getStartDate() != null &&
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
        if (request.getQuestion() != null) {
            logger.fine(() -> "Setting question to '" + request.getQuestion() + "'");
            existing.setQuestion(request.getQuestion());
        }
        if (request.getStartDate() != null) existing.setStartDate(java.sql.Date.valueOf(request.getStartDate()));
        if (request.getEndDate() != null) existing.setEndDate(java.sql.Date.valueOf(request.getEndDate()));
        if (request.getStatus() != null) {
            logger.fine(() -> "Updating status to '" + request.getStatus() + "'");
            existing.setStatus(request.getStatus());
        }
        ReferendumEntity saved = referendumRepository.save(existing);
        logger.info(() -> "Referendum updated id=" + saved.getId());
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public void deleteReferendum(Long id) {
        logger.info(() -> "Deleting referendum id=" + id);
        ReferendumEntity existing = referendumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Referendum not found"));
        if (existing.getStatus() != ReferendumStatus.DRAFT) {
            logger.warning("Attempt to delete non-draft referendum");
            throw new IllegalStateException("Can only delete referendums in DRAFT status");
        }
        referendumRepository.deleteById(id);
        logger.info(() -> "Deleted referendum id=" + id);
    }

    @Override
    public ReferendumResponseDTO getReferendumById(Long id) {
        logger.info(() -> "Fetching referendum id=" + id);
        ReferendumResponseDTO dto = referendumRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Referendum not found"));
        logger.fine(() -> "Fetched referendum title='" + dto.getTitle() + "'");
        return dto;
    }

    @Override
    public PagedResponseDTO<ReferendumResponseDTO> listReferendums(int page, int size, ReferendumStatus status) {
        logger.info(() -> String.format("Listing referendums page=%d size=%d status=%s", page, size, status));
        PageRequest pr = PageRequest.of(page, size);
        Page<ReferendumEntity> pageData = (status != null)
                ? referendumRepository.findByStatus(status, pr)
                : referendumRepository.findAll(pr);
        List<ReferendumResponseDTO> content = pageData.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        logger.fine(() -> "Listed referendums count=" + content.size());
        return new PagedResponseDTO<>(content, pageData.getNumber(), pageData.getSize(), pageData.getTotalElements(), pageData.getTotalPages(), pageData.isLast());
    }

    @Override
    @Transactional
    public ReferendumVoteResponseDTO castVote(ReferendumVoteRequestDTO request) {
        Long refId = request.getReferendumId();
        Long userId = getCurrentUserId();
        logger.info(() -> String.format("User %d casting vote for referendum %d, option %d", userId, refId, request.getOptionId()));
        ReferendumEntity referendum = referendumRepository.findById(refId)
                .orElseThrow(() -> new RuntimeException("Referendum not found"));
        if (referendum.getStatus() != ReferendumStatus.OPEN) {
            logger.warning("Attempt to vote in closed referendum");
            throw new IllegalStateException("Referendum is not open for voting");
        }
        if (voteRepository.existsByUserIdAndReferendum_Id(userId, refId)) {
            logger.warning("Duplicate vote attempt");
            throw new IllegalStateException("User has already voted in this referendum");
        }
        ReferendumOptionEntity option = optionRepository.findById(request.getOptionId())
                .orElseThrow(() -> new RuntimeException("Option not found"));
        if (!option.getReferendum().getId().equals(refId)) {
            logger.warning("Option mismatch for referendum");
            throw new IllegalArgumentException("Option does not belong to referendum");
        }
        option.setVoteCount(option.getVoteCount() + 1);
        optionRepository.save(option);
        ReferendumVoteEntity vote = new ReferendumVoteEntity();
        vote.setUserId(userId);
        vote.setReferendum(referendum);
        vote.setOption(option);
        vote.setVoteTimestamp(LocalDateTime.now());
        ReferendumVoteEntity saved = voteRepository.save(vote);
        logger.info(() -> "Referendum vote recorded id=" + saved.getId());
        return new ReferendumVoteResponseDTO(saved.getId(), saved.getUserId(), refId, option.getId(), saved.getVoteTimestamp());
    }

    @Override
    public ReferendumResultsDTO getResults(Long referendumId) {
        logger.info(() -> "Fetching referendum results for id=" + referendumId);
        ReferendumEntity referendum = referendumRepository.findById(referendumId)
                .orElseThrow(() -> new RuntimeException("Referendum not found"));
        List<OptionResultDTO> results = optionRepository.findByReferendum(referendum).stream()
                .map(opt -> new OptionResultDTO(opt.getId(), opt.getOptionText(), opt.getVoteCount()))
                .collect(Collectors.toList());
        logger.fine(() -> "Option results count=" + results.size());
        return new ReferendumResultsDTO(referendumId, results);
    }

    private ReferendumResponseDTO mapToResponse(ReferendumEntity e) {
        List<OptionResponseDTO> opts = e.getOptions().stream()
                .map(opt -> new OptionResponseDTO(opt.getId(), opt.getOptionText(), opt.getVoteCount(), e.getId()))
                .collect(Collectors.toList());
        return new ReferendumResponseDTO(e.getId(), e.getTitle(), e.getDescription(), e.getQuestion(), e.getStartDate().toLocalDate(), e.getEndDate().toLocalDate(), e.getStatus().name(), e.getCreatedBy().getId(), opts);
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ((com.tu.votingapp.security.UserPrincipal) auth.getPrincipal()).getId();
    }
}