package com.tu.votingapp.services.interfaces.referendum;

import com.tu.votingapp.dto.request.referendum.ReferendumRequestDTO;
import com.tu.votingapp.dto.request.referendum.ReferendumVoteRequestDTO;
import com.tu.votingapp.dto.response.PagedResponseDTO;
import com.tu.votingapp.dto.response.referendum.ReferendumResponseDTO;
import com.tu.votingapp.dto.response.referendum.ReferendumResultsDTO;
import com.tu.votingapp.dto.response.referendum.ReferendumVoteResponseDTO;
import com.tu.votingapp.enums.ReferendumStatus;

/**
 * Service interface for managing referendums and their votes.
 */
public interface ReferendumService {

    /**
     * Create a new referendum.
     */
    ReferendumResponseDTO createReferendum(ReferendumRequestDTO request);

    /**
     * Update an existing referendum.
     */
    ReferendumResponseDTO updateReferendum(ReferendumRequestDTO request);

    /**
     * Delete a referendum by its ID.
     */
    void deleteReferendum(Long id);

    /**
     * Get details of a single referendum by ID.
     */
    ReferendumResponseDTO getReferendumById(Long id);

    /**
     * List referendums with optional status filtering and pagination.
     *
     * @param page   zero-based page index
     * @param size   page size
     * @param status optional referendum status filter
     */
    PagedResponseDTO<ReferendumResponseDTO> listReferendums(
            int page,
            int size,
            ReferendumStatus status
    );

    /**
     * Cast a vote in a referendum.
     */
    ReferendumVoteResponseDTO castVote(ReferendumVoteRequestDTO request);

    /**
     * Get aggregated results for a referendum.
     */
    ReferendumResultsDTO getResults(Long referendumId);
}