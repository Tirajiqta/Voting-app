package com.tu.votingapp.controllers;

import com.tu.votingapp.dto.request.elections.CandidateRequestDTO;
import com.tu.votingapp.dto.request.elections.ElectionsRequestDTO;
import com.tu.votingapp.dto.request.elections.VoteRequestDTO;
import com.tu.votingapp.dto.response.PagedResponseDTO;
import com.tu.votingapp.dto.response.elections.CandidateResponseDTO;
import com.tu.votingapp.dto.response.elections.ElectionResponseDTO;
import com.tu.votingapp.dto.response.elections.ElectionResultsDTO;
import com.tu.votingapp.dto.response.elections.VoteResponseDTO;
import com.tu.votingapp.enums.ElectionStatus;
import com.tu.votingapp.enums.ElectionType;
import com.tu.votingapp.services.interfaces.elections.CandidateService;
import com.tu.votingapp.services.interfaces.elections.ElectionService;
import com.tu.votingapp.services.interfaces.elections.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api")
@Validated
@RequiredArgsConstructor
public class ElectionController {

    private final ElectionService electionService;
    private final CandidateService candidateService;
    private final VoteService voteService;
    private final Logger logger = Logger.getLogger(ElectionController.class.getName());

    // Election endpoints

    @PostMapping("/elections")
    public ResponseEntity<ElectionResponseDTO> createElection(
            @Valid @RequestBody ElectionsRequestDTO request) {
        logger.info(() -> "Creating election: " + request.getElectionName());
        ElectionResponseDTO response = electionService.createElection(request);
        logger.info(() -> "Created election with id=" + response.getId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/elections/{id}")
    public ResponseEntity<ElectionResponseDTO> updateElection(
            @PathVariable Long id,
            @Valid @RequestBody ElectionsRequestDTO request) {
        logger.info(() -> "Updating election id=" + id);
        request.setId(id);
        ElectionResponseDTO response = electionService.updateElection(request);
        logger.info(() -> "Updated election id=" + response.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/elections/{id}")
    public ResponseEntity<Void> deleteElection(@PathVariable Long id) {
        logger.info(() -> "Deleting election id=" + id);
        electionService.deleteElection(id);
        logger.info(() -> "Deleted election id=" + id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/elections/{id}")
    public ResponseEntity<ElectionResponseDTO> getElection(@PathVariable Long id) {
        logger.info(() -> "Fetching election id=" + id);
        ElectionResponseDTO response = electionService.getElectionById(id);
        logger.fine(() -> "Fetched election: " + response.getElectionName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/elections")
    public ResponseEntity<PagedResponseDTO<ElectionResponseDTO>> listElections(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) ElectionStatus status,
            @RequestParam(required = false) ElectionType type
    ) {
        logger.info(() -> String.format(
                "Listing elections: page=%d, size=%d, status=%s, type=%s",
                page, size, status, type
        ));
        PagedResponseDTO<ElectionResponseDTO> response =
                electionService.listElections(page, size, status, type);
        logger.fine(() -> String.format(
                "Listed %d elections (page %d)", response.getContent().size(), response.getPage()
        ));
        return ResponseEntity.ok(response);
    }

    // Candidate endpoints

    @PostMapping("/elections/{electionId}/candidates")
    public ResponseEntity<CandidateResponseDTO> addCandidate(
            @PathVariable Long electionId,
            @Valid @RequestBody CandidateRequestDTO request) {
        logger.info(() -> "Adding candidate to election id=" + electionId + ", name=" + request.getName());
        request.setElectionId(electionId);
        CandidateResponseDTO response = candidateService.createCandidate(request);
        logger.info(() -> "Added candidate id=" + response.getId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/candidates/{id}")
    public ResponseEntity<CandidateResponseDTO> updateCandidate(
            @PathVariable Long id,
            @Valid @RequestBody CandidateRequestDTO request) {
        logger.info(() -> "Updating candidate id=" + id);
        request.setId(id);
        CandidateResponseDTO response = candidateService.updateCandidate(request);
        logger.info(() -> "Updated candidate id=" + response.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/candidates/{id}")
    public ResponseEntity<Void> deleteCandidate(@PathVariable Long id) {
        logger.info(() -> "Deleting candidate id=" + id);
        candidateService.deleteCandidate(id);
        logger.info(() -> "Deleted candidate id=" + id);
        return ResponseEntity.noContent().build();
    }

    // Vote endpoints

    @PostMapping("/votes")
    public ResponseEntity<VoteResponseDTO> castVote(
            @Valid @RequestBody VoteRequestDTO request) {
        logger.info(() -> "Casting vote for election=" + request.getElectionId()
                + ", candidateId=" + request.getCandidateId()
                + ", partyId=" + request.getPartyId());
        VoteResponseDTO response = voteService.castVote(request);
        logger.info(() -> "Vote recorded id=" + response.getId());
        return ResponseEntity.ok(response);
    }

    // Results endpoint

    @GetMapping("/elections/{electionId}/results")
    public ResponseEntity<ElectionResultsDTO> getResults(@PathVariable Long electionId) {
        logger.info(() -> "Fetching results for election id=" + electionId);
        ElectionResultsDTO response = electionService.getResults(electionId);
        logger.fine(() -> String.format("Results fetched: %d candidate results, %d party results",
                response.getCandidateResults().size(), response.getPartyResults().size()));
        return ResponseEntity.ok(response);
    }
}