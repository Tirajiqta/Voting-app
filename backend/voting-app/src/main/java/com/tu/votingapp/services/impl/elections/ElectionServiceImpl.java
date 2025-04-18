package com.tu.votingapp.services.impl.elections;

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
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ElectionServiceImpl implements ElectionService {
    private final ElectionRepository electionRepository;
    private final PartyVoteRepository partyVoteRepository;
    private final ElectionMapper electionMapper;
    private final CandidateMapper candidateMapper;
    private final PartyMapper partyMapper;
    private final Logger logger = Logger.getLogger(ElectionServiceImpl.class.getName());

    @Override
    @Transactional
    public ElectionResponseDTO createElection(ElectionsRequestDTO request) {
        logger.info(() -> "Creating election: name='" + request.getElectionName() + "', type='" + request.getElectionType() + "', status='" + request.getStatus() + "'");
        LocalDate now = LocalDate.now();
        if (request.getStartDate().isBefore(now)) {
            throw new IllegalArgumentException("startDate cannot be in the past");
        }
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("endDate must be after startDate");
        }
        if (!(request.getStatus() == ElectionStatus.DRAFT || request.getStatus() == ElectionStatus.SCHEDULED)) {
            throw new IllegalArgumentException("status must be DRAFT or SCHEDULED when creating an election");
        }
        // Build entity
        ElectionEntity entity = electionMapper.toEntity(new com.tu.votingapp.dto.general.elections.ElectionDTO(
                null,
                request.getElectionName(),
                request.getDescription(),
                Date.valueOf(request.getStartDate()),
                Date.valueOf(request.getEndDate()),
                request.getElectionType().name(),
                request.getStatus().name(),
                List.of(),
                getCurrentUserId()
        ));
        ElectionEntity saved = electionRepository.save(entity);
        logger.info(() -> "Election created with id=" + saved.getId());
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public ElectionResponseDTO updateElection(ElectionsRequestDTO request) {
        logger.info(() -> "Updating election id=" + request.getId());
        ElectionEntity existing = electionRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Election not found: " + request.getId()));
        LocalDate now = LocalDate.now();
        ElectionStatus oldStatus = existing.getStatus();
        ElectionStatus newStatus = request.getStatus();
        // Validate transitions
        if (newStatus != null && newStatus != oldStatus) {
            logger.fine(() -> "Status changing from " + oldStatus + " to " + newStatus);
            // ... status transition logic ...
            existing.setStatus(newStatus);
        }
        // Partial fields
        if (request.getElectionName() != null) existing.setElectionName(request.getElectionName());
        if (request.getDescription() != null) existing.setDescription(request.getDescription());
        if (request.getStartDate() != null) existing.setStartDate(Date.valueOf(request.getStartDate()));
        if (request.getEndDate() != null) existing.setEndDate(Date.valueOf(request.getEndDate()));
        if (request.getElectionType() != null) existing.setElectionType(request.getElectionType());

        ElectionEntity saved = electionRepository.save(existing);
        logger.info(() -> "Election updated id=" + saved.getId());
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public void deleteElection(Long id) {
        logger.info(() -> "Deleting election id=" + id);
        ElectionEntity existing = electionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Election not found: " + id));
        if (existing.getStatus() != ElectionStatus.DRAFT) {
            throw new IllegalStateException("Can only delete elections in DRAFT status");
        }
        electionRepository.deleteById(id);
        logger.info(() -> "Deleted election id=" + id);
    }

    @Override
    public ElectionResponseDTO getElectionById(Long id) {
        logger.info(() -> "Fetching election id=" + id);
        ElectionResponseDTO dto = electionRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Election not found: " + id));
        logger.fine(() -> "Fetched election: name='" + dto.getElectionName() + "'");
        return dto;
    }

    @Override
    public PagedResponseDTO<ElectionResponseDTO> listElections(int page, int size,
                                                               ElectionStatus status,
                                                               ElectionType type) {
        logger.info(() -> String.format("Listing elections page=%d size=%d status=%s type=%s", page, size, status, type));
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
        logger.fine(() -> "Listed elections count=" + content.size());
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
        logger.info(() -> "Fetching results for election id=" + electionId);
        ElectionEntity election = electionRepository.findById(electionId)
                .orElseThrow(() -> new RuntimeException("Election not found: " + electionId));
        List<CandidateResultDTO> candResults = election.getCandidates().stream()
                .map(c -> new CandidateResultDTO(c.getId(), c.getName(), c.getVotesCount()))
                .collect(Collectors.toList());
        List<PartyResultDTO> partyResults = partyVoteRepository.findByElection(election).stream()
                .map(pv -> new PartyResultDTO(pv.getParty().getId(), pv.getParty().getName(), pv.getVoteCount()))
                .collect(Collectors.toList());
        logger.fine(() -> String.format("Results: %d candidateResults, %d partyResults", candResults.size(), partyResults.size()));
        return new ElectionResultsDTO(electionId, candResults, partyResults);
    }

    private ElectionResponseDTO mapToResponse(ElectionEntity e) {
        List<CandidateResponseDTO> cands = e.getCandidates().stream()
                .map(c -> new CandidateResponseDTO(
                        c.getId(), c.getName(), c.getBio(), e.getId(), c.getVotesCount(), c.getImageUri(), c.getPosition()))
                .collect(Collectors.toList());
        List<PartyResponseDTO> parties = e.getParties().stream()
                .map(p -> new PartyResponseDTO(
                        p.getId(), p.getName(), p.getAbbreviation(), p.getLogoUrl(), p.getLeaderName(),
                        p.getCandidates().stream()
                                .map(c -> new CandidateResponseDTO(
                                        c.getId(), c.getName(), c.getBio(), e.getId(), c.getVotesCount(), c.getImageUri(), c.getPosition()))
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
        return new ElectionResponseDTO(
                e.getId(), e.getElectionName(), e.getDescription(), e.getStartDate().toLocalDate(), e.getEndDate().toLocalDate(),
                e.getElectionType().name(), e.getStatus().name(), e.getCreatedBy().getId(), cands, parties
        );
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ((com.tu.votingapp.security.UserPrincipal) auth.getPrincipal()).getId();
    }
}