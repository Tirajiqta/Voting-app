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

@RestController
@RequestMapping("/api/referendums")
@Validated
@RequiredArgsConstructor
public class ReferendumController {

    private final ReferendumService referendumService;

    /**
     * Create a new referendum.
     */
    @PostMapping
    @Validated(ValidationGroups.Create.class)
    public ResponseEntity<ReferendumResponseDTO> createReferendum(
            @Valid @RequestBody ReferendumRequestDTO request
    ) {
        ReferendumResponseDTO response = referendumService.createReferendum(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Update an existing referendum.
     */
    @PutMapping("/{id}")
    @Validated(ValidationGroups.Update.class)
    public ResponseEntity<ReferendumResponseDTO> updateReferendum(
            @PathVariable Long id,
            @Valid @RequestBody ReferendumRequestDTO request
    ) {
        request.setId(id);
        ReferendumResponseDTO response = referendumService.updateReferendum(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a referendum.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReferendum(@PathVariable Long id) {
        referendumService.deleteReferendum(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get a single referendum by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReferendumResponseDTO> getReferendum(@PathVariable Long id) {
        ReferendumResponseDTO response = referendumService.getReferendumById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * List referendums with pagination and optional status filter.
     */
    @GetMapping
    public ResponseEntity<PagedResponseDTO<ReferendumResponseDTO>> listReferendums(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) ReferendumStatus status
    ) {
        PagedResponseDTO<ReferendumResponseDTO> response =
                referendumService.listReferendums(page, size, status);
        return ResponseEntity.ok(response);
    }

    /**
     * Cast a vote in a referendum.
     */
    @PostMapping("/{id}/votes")
    public ResponseEntity<ReferendumVoteResponseDTO> castVote(
            @PathVariable("id") Long referendumId,
            @Valid @RequestBody ReferendumVoteRequestDTO request
    ) {
        // ensure path and payload IDs match (optional)
        if (!referendumId.equals(request.getReferendumId())) {
            throw new IllegalArgumentException("Path referendumId must match request payload");
        }
        ReferendumVoteResponseDTO response = referendumService.castVote(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Get aggregated results for a referendum.
     */
    @GetMapping("/{id}/results")
    public ResponseEntity<ReferendumResultsDTO> getResults(@PathVariable Long id) {
        ReferendumResultsDTO response = referendumService.getResults(id);
        return ResponseEntity.ok(response);
    }
}
