package com.tu.votingapp.services.impl.elections;

import com.tu.votingapp.dto.request.elections.CandidateRequestDTO;
import com.tu.votingapp.dto.response.elections.CandidateResponseDTO;
import com.tu.votingapp.entities.elections.CandidateEntity;
import com.tu.votingapp.entities.elections.ElectionEntity;
import com.tu.votingapp.repositories.interfaces.elections.CandidateRepository;
import com.tu.votingapp.repositories.interfaces.elections.ElectionRepository;
import com.tu.votingapp.services.interfaces.elections.CandidateService;
import com.tu.votingapp.utils.mappers.election.CandidateMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class CandidateServiceImpl implements CandidateService {

    private final CandidateRepository candidateRepository;
    private final ElectionRepository electionRepository;
    private final CandidateMapper candidateMapper;
    private final Logger logger = Logger.getLogger(CandidateServiceImpl.class.getName());

    @Override
    @Transactional
    public CandidateResponseDTO createCandidate(CandidateRequestDTO request) {
        logger.info(() -> "Creating candidate: name='" + request.getName() + "' for electionId=" + request.getElectionId());
        ElectionEntity election = electionRepository.findById(request.getElectionId())
                .orElseThrow(() -> new RuntimeException("Election not found: " + request.getElectionId()));

        com.tu.votingapp.dto.general.elections.CandidateDTO dto = new com.tu.votingapp.dto.general.elections.CandidateDTO(
                null,
                request.getName(),
                request.getBio() != null ? request.getBio().substring(0, Math.min(50, request.getBio().length())) + "..." : null,
                request.getElectionId(),
                0,
                request.getImageUri(),
                request.getPosition()
        );

        CandidateEntity entity = candidateMapper.toEntity(dto);
        entity.setElection(election);
        CandidateEntity saved = candidateRepository.save(entity);

        logger.info(() -> "Candidate created with id=" + saved.getId());
        return new CandidateResponseDTO(
                saved.getId(),
                saved.getName(),
                saved.getBio(),
                saved.getElection().getId(),
                saved.getVotesCount(),
                saved.getImageUri(),
                saved.getPosition()
        );
    }

    @Override
    @Transactional
    public CandidateResponseDTO updateCandidate(CandidateRequestDTO request) {
        logger.info(() -> "Updating candidate id=" + request.getId());
        CandidateEntity existing = candidateRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Candidate not found: " + request.getId()));

        if (request.getName() != null) {
            logger.fine(() -> " - Setting name to '" + request.getName() + "'");
            existing.setName(request.getName());
        }
        if (request.getBio() != null) {
            logger.fine(() -> " - Updating bio (truncated for logging)");
            existing.setBio(request.getBio());
        }
        if (request.getImageUri() != null) {
            logger.fine(() -> " - Updating image URI");
            existing.setImageUri(request.getImageUri());
        }
        if (request.getPosition() != null) {
            logger.fine(() -> " - Setting position to '" + request.getPosition() + "'");
            existing.setPosition(request.getPosition());
        }

        CandidateEntity saved = candidateRepository.save(existing);
        logger.info(() -> "Candidate updated id=" + saved.getId());
        return new CandidateResponseDTO(
                saved.getId(),
                saved.getName(),
                saved.getBio(),
                saved.getElection().getId(),
                saved.getVotesCount(),
                saved.getImageUri(),
                saved.getPosition()
        );
    }

    @Override
    @Transactional
    public void deleteCandidate(Long id) {
        logger.info(() -> "Deleting candidate id=" + id);
        candidateRepository.deleteById(id);
        logger.info(() -> "Deleted candidate id=" + id);
    }
}
