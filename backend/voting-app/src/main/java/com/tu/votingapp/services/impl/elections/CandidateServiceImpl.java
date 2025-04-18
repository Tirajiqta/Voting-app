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

@Service
@RequiredArgsConstructor
public class CandidateServiceImpl implements CandidateService {

    private final CandidateRepository candidateRepository;
    private final ElectionRepository electionRepository;
    private final CandidateMapper candidateMapper;

    @Override
    @Transactional
    public CandidateResponseDTO createCandidate(CandidateRequestDTO request) {
        ElectionEntity election = electionRepository.findById(request.getElectionId())
                .orElseThrow(() -> new RuntimeException("Election not found"));
        // Map Request -> General DTO
        com.tu.votingapp.dto.general.elections.CandidateDTO dto = new com.tu.votingapp.dto.general.elections.CandidateDTO(
                null,
                request.getName(),
                request.getBio(),
                request.getElectionId(),
                0,
                request.getImageUri(),
                request.getPosition()
        );
        // Map DTO -> Entity (sets election reference via mapper)
        CandidateEntity entity = candidateMapper.toEntity(dto);
        entity.setElection(election);
        CandidateEntity saved = candidateRepository.save(entity);
        // Map Entity -> Response
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
        CandidateEntity existing = candidateRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Candidate not found"));
        if (request.getName()    != null) existing.setName(request.getName());
        if (request.getBio()     != null) existing.setBio(request.getBio());
        if (request.getImageUri()!= null) existing.setImageUri(request.getImageUri());
        if (request.getPosition()!= null) existing.setPosition(request.getPosition());
        CandidateEntity saved = candidateRepository.save(existing);
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
    public void deleteCandidate(Long id) {
        candidateRepository.deleteById(id);
    }
}
