package com.tu.votingapp.services.interfaces.elections;

import com.tu.votingapp.dto.request.elections.CandidateRequestDTO;
import com.tu.votingapp.dto.response.elections.CandidateResponseDTO;

public interface CandidateService {
    CandidateResponseDTO createCandidate(CandidateRequestDTO request);
    CandidateResponseDTO updateCandidate(CandidateRequestDTO request);
    void deleteCandidate(Long id);
}
