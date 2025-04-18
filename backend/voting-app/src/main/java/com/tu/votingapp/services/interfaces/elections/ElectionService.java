package com.tu.votingapp.services.interfaces.elections;

import com.tu.votingapp.dto.request.elections.ElectionsRequestDTO;
import com.tu.votingapp.dto.response.PagedResponseDTO;
import com.tu.votingapp.dto.response.elections.ElectionResponseDTO;
import com.tu.votingapp.dto.response.elections.ElectionResultsDTO;
import com.tu.votingapp.enums.ElectionStatus;
import com.tu.votingapp.enums.ElectionType;

public interface ElectionService {
    ElectionResponseDTO createElection(ElectionsRequestDTO request);
    ElectionResponseDTO updateElection(ElectionsRequestDTO request);
    void deleteElection(Long id);
    ElectionResponseDTO getElectionById(Long id);
    PagedResponseDTO<ElectionResponseDTO> listElections(int page, int size,
                                                        ElectionStatus status,
                                                        ElectionType type);
    ElectionResultsDTO getResults(Long electionId);
}
