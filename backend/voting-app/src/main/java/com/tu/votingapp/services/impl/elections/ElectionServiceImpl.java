package com.tu.votingapp.services.impl.elections;

import com.tu.votingapp.dto.general.elections.ElectionDTO;
import com.tu.votingapp.dto.request.elections.ElectionsRequestDTO;
import com.tu.votingapp.dto.response.PagedResponseDTO;
import com.tu.votingapp.dto.response.elections.*;
import com.tu.votingapp.entities.elections.ElectionEntity;
import com.tu.votingapp.enums.ElectionStatus;
import com.tu.votingapp.enums.ElectionType;
import com.tu.votingapp.repositories.interfaces.elections.ElectionRepository;
import com.tu.votingapp.repositories.interfaces.elections.PartyVoteRepository;
import com.tu.votingapp.services.interfaces.elections.ElectionService;
import com.tu.votingapp.utils.mappers.election.CandidateMapper;
import com.tu.votingapp.utils.mappers.election.ElectionMapper;
import com.tu.votingapp.utils.mappers.election.PartyMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ElectionServiceImpl implements ElectionService {
    private final ElectionRepository electionRepository;
    private final PartyVoteRepository partyVoteRepository;
    private final ElectionMapper electionMapper;
    private final CandidateMapper candidateMapper;
    private final PartyMapper partyMapper;

    @Override
    @Transactional
    public ElectionResponseDTO createElection(ElectionsRequestDTO request) {
        LocalDate now = LocalDate.now();
        // Timeline validation
        if (request.getStartDate().isBefore(now)) {
            throw new IllegalArgumentException("startDate cannot be in the past");
        }
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("endDate must be after startDate");
        }
        // Initial status must be DRAFT or SCHEDULED
        if (!(request.getStatus() == ElectionStatus.DRAFT ||
                request.getStatus() == ElectionStatus.SCHEDULED)) {
            throw new IllegalArgumentException(
                    "status must be DRAFT or SCHEDULED when creating an election");
        }
        // Map RequestDTO -> Entity
        ElectionEntity entity = electionMapper.toEntity(
                electionMapper.toDto(
                        electionMapper.toEntity(new com.tu.votingapp.dto.general.elections.ElectionDTO(
                                null,
                                request.getElectionName(),
                                request.getDescription(),
                                Date.valueOf(request.getStartDate()),
                                Date.valueOf(request.getEndDate()),
                                request.getElectionType().name(),
                                request.getStatus().name(),
                                List.of(),
                                getCurrentUserId()
                        ))));
        // Persist
        ElectionEntity saved = electionRepository.save(entity);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public ElectionResponseDTO updateElection(ElectionsRequestDTO request) {
        ElectionEntity existing = electionRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Election not found"));
        LocalDate now = LocalDate.now();
        ElectionStatus oldStatus = existing.getStatus();
        ElectionStatus newStatus = request.getStatus();

        // Restrict timeline/type modifications to DRAFT only
        if ((request.getStartDate() != null ||
                request.getEndDate()   != null ||
                request.getElectionType() != null) &&
                oldStatus != ElectionStatus.DRAFT) {
            throw new IllegalStateException(
                    "Cannot modify startDate, endDate, or electionType unless election is in DRAFT status");
        }

        // Handle status transitions
        if (newStatus != null && newStatus != oldStatus) {
            switch (oldStatus) {
                case DRAFT:
                    if (newStatus == ElectionStatus.SCHEDULED) {
                        LocalDate start = request.getStartDate() != null
                                ? request.getStartDate()
                                : existing.getStartDate().toLocalDate();
                        if (start.isBefore(now)) {
                            throw new IllegalStateException(
                                    "Cannot schedule election in the past");
                        }
                    } else {
                        throw new IllegalStateException(
                                "Can only transition from DRAFT to SCHEDULED");
                    }
                    break;
                case SCHEDULED:
                    if (newStatus == ElectionStatus.OPEN) {
                        LocalDate start = existing.getStartDate().toLocalDate();
                        LocalDate end = existing.getEndDate().toLocalDate();
                        if (now.isBefore(start) || now.isAfter(end)) {
                            throw new IllegalStateException(
                                    "Cannot open election outside the scheduled period");
                        }
                    } else {
                        throw new IllegalStateException(
                                "Can only transition from SCHEDULED to OPEN");
                    }
                    break;
                case OPEN:
                    if (newStatus == ElectionStatus.CLOSED) {
                        LocalDate end = existing.getEndDate().toLocalDate();
                        if (now.isBefore(end)) {
                            throw new IllegalStateException(
                                    "Cannot close election before its endDate");
                        }
                    } else {
                        throw new IllegalStateException(
                                "Can only transition from OPEN to CLOSED");
                    }
                    break;
                case CLOSED:
                    throw new IllegalStateException(
                            "Cannot change status once election is CLOSED");
            }
            existing.setStatus(newStatus);
        }

        // Partial updates for other fields
        if (request.getElectionName() != null) existing.setElectionName(request.getElectionName());
        if (request.getDescription()  != null) existing.setDescription(request.getDescription());
        if (request.getStartDate()    != null) existing.setStartDate(Date.valueOf(request.getStartDate()));
        if (request.getEndDate()      != null) existing.setEndDate(Date.valueOf(request.getEndDate()));
        if (request.getElectionType() != null) existing.setElectionType(request.getElectionType());

        ElectionEntity saved = electionRepository.save(existing);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public void deleteElection(Long id) {
        ElectionEntity existing = electionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Election not found"));
        if (existing.getStatus() != ElectionStatus.DRAFT) {
            throw new IllegalStateException(
                    "Can only delete elections in DRAFT status");
        }
        electionRepository.deleteById(id);
    }

    @Override
    public ElectionResponseDTO getElectionById(Long id) {
        return electionRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Election not found"));
    }

    @Override
    public PagedResponseDTO<ElectionResponseDTO> listElections(int page, int size,
                                                               ElectionStatus status,
                                                               ElectionType type) {
        PageRequest pr = PageRequest.of(page, size);
        Page<ElectionEntity> pageData;
        if (status != null && type != null) {
            pageData = electionRepository.findByStatusAndElectionType(status, type, pr);
        } else if (status != null) {
            pageData = electionRepository.findByStatus(status, pr);
        } else if (type != null) {
            pageData = electionRepository.findByElectionType(type, pr);
        } else {
            pageData = electionRepository.findAll(pr);
        }
        List<ElectionResponseDTO> content = pageData.getContent().stream()
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
    public ElectionResultsDTO getResults(Long electionId) {
        ElectionEntity election = electionRepository.findById(electionId)
                .orElseThrow(() -> new RuntimeException("Election not found"));
        List<CandidateResultDTO> candResults = election.getCandidates().stream()
                .map(c -> new CandidateResultDTO(
                        c.getId(), c.getName(), c.getVotesCount()))
                .collect(Collectors.toList());
        List<PartyResultDTO> partyResults = partyVoteRepository.findByElection(election).stream()
                .map(pv -> new PartyResultDTO(
                        pv.getParty().getId(), pv.getParty().getName(), pv.getVoteCount()))
                .collect(Collectors.toList());
        return new ElectionResultsDTO(electionId, candResults, partyResults);
    }

    private ElectionResponseDTO mapToResponse(ElectionEntity e) {
        List<CandidateResponseDTO> cands = e.getCandidates().stream()
                .map(c -> new CandidateResponseDTO(
                        c.getId(), c.getName(), c.getBio(),
                        e.getId(), c.getVotesCount(),
                        c.getImageUri(), c.getPosition()))
                .collect(Collectors.toList());
        List<PartyResponseDTO> parties = e.getParties().stream()
                .map(p -> new PartyResponseDTO(
                        p.getId(), p.getName(), p.getAbbreviation(),
                        p.getLogoUrl(), p.getLeaderName(),
                        p.getCandidates().stream()
                                .map(c -> new CandidateResponseDTO(
                                        c.getId(), c.getName(), c.getBio(),
                                        e.getId(), c.getVotesCount(),
                                        c.getImageUri(), c.getPosition()))
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
        return new ElectionResponseDTO(
                e.getId(), e.getElectionName(), e.getDescription(),
                e.getStartDate().toLocalDate(), e.getEndDate().toLocalDate(),
                e.getElectionType().name(), e.getStatus().name(),
                e.getCreatedBy().getId(), cands, parties
        );
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ((com.tu.votingapp.security.UserPrincipal) auth.getPrincipal()).getId();
    }
}

