package com.tu.votingapp.controllers;

import com.tu.votingapp.dto.request.referendum.ReferendumRequestDTO;
import com.tu.votingapp.dto.request.referendum.ReferendumVoteRequestDTO;
import com.tu.votingapp.dto.response.PagedResponseDTO;
import com.tu.votingapp.dto.response.referendum.ReferendumResponseDTO;
import com.tu.votingapp.dto.response.referendum.ReferendumResultsDTO;
import com.tu.votingapp.dto.response.referendum.ReferendumVoteResponseDTO;
import com.tu.votingapp.enums.ReferendumStatus;
import com.tu.votingapp.services.interfaces.referendum.ReferendumService;
import com.tu.votingapp.validation.ValidationGroups;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

/**
 * REST endpoints for managing referendums and voting.
 */
@RestController
@RequestMapping("/api/referendums")
@Validated
@RequiredArgsConstructor
public class ReferendumController {

    private final ReferendumService referendumService;
    private final Logger logger = Logger.getLogger(ReferendumController.class.getName());

    /**
     * Create a new referendum.
     */
    @PostMapping
    @Validated(ValidationGroups.Create.class)
    public ResponseEntity<ReferendumResponseDTO> createReferendum(
            @Valid @RequestBody ReferendumRequestDTO request) {
        logger.info(() -> "Creating referendum: title='" + request.getTitle() + "'");
        ReferendumResponseDTO response = referendumService.createReferendum(request);
        logger.info(() -> "Created referendum id=" + response.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * Update an existing referendum.
     */
    @PutMapping("/{id}")
    @Validated(ValidationGroups.Update.class)
    public ResponseEntity<ReferendumResponseDTO> updateReferendum(
            @PathVariable Long id,
            @Valid @RequestBody ReferendumRequestDTO request) {
        logger.info(() -> "Updating referendum id=" + id);
        request.setId(id);
        ReferendumResponseDTO response = referendumService.updateReferendum(request);
        logger.info(() -> "Updated referendum id=" + response.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a referendum.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReferendum(@PathVariable Long id) {
        logger.info(() -> "Deleting referendum id=" + id);
        referendumService.deleteReferendum(id);
        logger.info(() -> "Deleted referendum id=" + id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get a single referendum by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReferendumResponseDTO> getReferendum(@PathVariable Long id) {
        logger.info(() -> "Fetching referendum id=" + id);
        ReferendumResponseDTO response = referendumService.getReferendumById(id);
        logger.fine(() -> "Fetched referendum: '" + response.getTitle() + "'");
        return ResponseEntity.ok(response);
    }

    /**
     * List referendums with pagination and optional status filter.
     */
    @GetMapping
    public ResponseEntity<PagedResponseDTO<ReferendumResponseDTO>> listReferendums(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) ReferendumStatus status) {
        logger.info(() -> String.format("Listing referendums: page=%d, size=%d, status=%s", page, size, status));
        PagedResponseDTO<ReferendumResponseDTO> pageDto =
                referendumService.listReferendums(page, size, status);
        logger.fine(() -> String.format("Listed %d referendums on page %d", pageDto.getContent().size(), pageDto.getPage()));
        return ResponseEntity.ok(pageDto);
    }

    /**
     * Cast a vote in a referendum.
     */
    @PostMapping("/{id}/votes")
    public ResponseEntity<ReferendumVoteResponseDTO> castVote(
            @PathVariable("id") Long referendumId,
            @Valid @RequestBody ReferendumVoteRequestDTO request) {
        logger.info(() -> "Casting vote in referendum id=" + referendumId +
                ", optionId=" + request.getOptionId());
        ReferendumVoteResponseDTO response = referendumService.castVote(request);
        logger.info(() -> "Vote recorded id=" + response.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * Get aggregated results for a referendum.
     */
    @GetMapping("/{id}/results")
    public ResponseEntity<ReferendumResultsDTO> getResults(@PathVariable Long id) {
        logger.info(() -> "Fetching results for referendum id=" + id);
        ReferendumResultsDTO response = referendumService.getResults(id);
        logger.fine(() -> String.format("Results: %d option results", response.getOptionResults().size()));
        return ResponseEntity.ok(response);
    }
}