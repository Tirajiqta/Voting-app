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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReferendumServiceImpl implements ReferendumService {
    private final ReferendumRepository referendumRepository;
    private final ReferendumOptionRepository optionRepository;
    private final ReferendumVoteRepository voteRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ReferendumResponseDTO createReferendum(ReferendumRequestDTO request) {
        LocalDate now = LocalDate.now();
        if (request.getStartDate().isBefore(now)) {
            throw new IllegalArgumentException("startDate cannot be in the past");
        }
        if (!request.getEndDate().isAfter(request.getStartDate())) {
            throw new IllegalArgumentException("endDate must be after startDate");
        }
        if (!(request.getStatus() == ReferendumStatus.DRAFT ||
                request.getStatus() == ReferendumStatus.SCHEDULED)) {
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
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public ReferendumResponseDTO updateReferendum(ReferendumRequestDTO request) {
        ReferendumEntity existing = referendumRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Referendum not found"));
        LocalDate now = LocalDate.now();
        if (request.getStartDate() != null && request.getStartDate().isBefore(now)) {
            throw new IllegalArgumentException("startDate cannot be in the past");
        }
        if (request.getEndDate() != null && request.getStartDate() != null &&
                !request.getEndDate().isAfter(request.getStartDate())) {
            throw new IllegalArgumentException("endDate must be after startDate");
        }
        if (request.getTitle() != null)       existing.setTitle(request.getTitle());
        if (request.getDescription() != null) existing.setDescription(request.getDescription());
        if (request.getQuestion() != null)    existing.setQuestion(request.getQuestion());
        if (request.getStartDate() != null)   existing.setStartDate(java.sql.Date.valueOf(request.getStartDate()));
        if (request.getEndDate() != null)     existing.setEndDate(java.sql.Date.valueOf(request.getEndDate()));
        if (request.getStatus() != null)      existing.setStatus(request.getStatus());
        ReferendumEntity saved = referendumRepository.save(existing);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public void deleteReferendum(Long id) {
        ReferendumEntity existing = referendumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Referendum not found"));
        if (existing.getStatus() != ReferendumStatus.DRAFT) {
            throw new IllegalStateException("Can only delete referendums in DRAFT status");
        }
        referendumRepository.deleteById(id);
    }

    @Override
    public ReferendumResponseDTO getReferendumById(Long id) {
        return referendumRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Referendum not found"));
    }

    @Override
    public PagedResponseDTO<ReferendumResponseDTO> listReferendums(int page, int size, ReferendumStatus status) {
        PageRequest pr = PageRequest.of(page, size);
        Page<ReferendumEntity> pageData = (status != null)
                ? referendumRepository.findByStatus(status, pr)
                : referendumRepository.findAll(pr);
        List<ReferendumResponseDTO> content = pageData.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return new PagedResponseDTO<>(
                content,
                pageData.getNumber(),
                pageData.getSize(),
                pageData.getTotalElements(),
                pageData.getTotalPages(),
                pageData.isLast()
        );
    }

    @Override
    @Transactional
    public ReferendumVoteResponseDTO castVote(ReferendumVoteRequestDTO request) {
        // Load referendum and validate OPEN status
        ReferendumEntity referendum = referendumRepository.findById(request.getReferendumId())
                .orElseThrow(() -> new RuntimeException("Referendum not found"));
        if (referendum.getStatus() != ReferendumStatus.OPEN) {
            throw new IllegalStateException("Referendum is not open for voting");
        }
        // Enforce one vote per user
        Long userId = getCurrentUserId();
        if (voteRepository.existsByUserIdAndReferendum_Id(userId, referendum.getId())) {
            throw new IllegalStateException("User has already voted in this referendum");
        }
        // Validate option and affiliation
        ReferendumOptionEntity option = optionRepository.findById(request.getOptionId())
                .orElseThrow(() -> new RuntimeException("Option not found"));
        if (!option.getReferendum().getId().equals(referendum.getId())) {
            throw new IllegalArgumentException("Option does not belong to referendum");
        }
        // Tally option
        option.setVoteCount(option.getVoteCount() + 1);
        optionRepository.save(option);
        // Persist vote
        ReferendumVoteEntity vote = new ReferendumVoteEntity();
        vote.setUserId(userId);
        vote.setReferendum(referendum);
        vote.setOption(option);
        vote.setVoteTimestamp(LocalDateTime.now());
        ReferendumVoteEntity saved = voteRepository.save(vote);
        // Map to response
        return new ReferendumVoteResponseDTO(
                saved.getId(),
                saved.getUserId(),
                referendum.getId(),
                option.getId(),
                saved.getVoteTimestamp()
        );
    }

    @Override
    public ReferendumResultsDTO getResults(Long referendumId) {
        ReferendumEntity referendum = referendumRepository.findById(referendumId)
                .orElseThrow(() -> new RuntimeException("Referendum not found"));
        List<OptionResultDTO> results = optionRepository.findByReferendum(referendum).stream()
                .map(opt -> new OptionResultDTO(
                        opt.getId(), opt.getOptionText(), opt.getVoteCount()))
                .collect(Collectors.toList());
        return new ReferendumResultsDTO(referendumId, results);
    }

    private ReferendumResponseDTO mapToResponse(ReferendumEntity e) {
        List<OptionResponseDTO> opts = e.getOptions().stream()
                .map(opt -> new OptionResponseDTO(
                        opt.getId(), opt.getOptionText(), opt.getVoteCount(), e.getId()))
                .collect(Collectors.toList());
        return new ReferendumResponseDTO(
                e.getId(),
                e.getTitle(),
                e.getDescription(),
                e.getQuestion(),
                e.getStartDate().toLocalDate(),
                e.getEndDate().toLocalDate(),
                e.getStatus().name(),
                e.getCreatedBy().getId(),
                opts
        );
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ((com.tu.votingapp.security.UserPrincipal) auth.getPrincipal()).getId();
    }
}

