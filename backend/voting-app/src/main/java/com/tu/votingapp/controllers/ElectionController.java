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


@RestController
@RequestMapping("/api")
@Validated
@RequiredArgsConstructor
public class ElectionController {

    private final ElectionService electionService;
    private final CandidateService candidateService;
    private final VoteService voteService;

    // Election endpoints

    @PostMapping("/elections")
    public ResponseEntity<ElectionResponseDTO> createElection(
            @Valid @RequestBody ElectionsRequestDTO request) {
        ElectionResponseDTO response = electionService.createElection(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/elections/{id}")
    public ResponseEntity<ElectionResponseDTO> updateElection(
            @PathVariable Long id,
            @Valid @RequestBody ElectionsRequestDTO request) {
        request.setId(id);
        ElectionResponseDTO response = electionService.updateElection(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/elections/{id}")
    public ResponseEntity<Void> deleteElection(@PathVariable Long id) {
        electionService.deleteElection(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/elections/{id}")
    public ResponseEntity<ElectionResponseDTO> getElection(@PathVariable Long id) {
        ElectionResponseDTO response = electionService.getElectionById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/elections")
    public ResponseEntity<PagedResponseDTO<ElectionResponseDTO>> listElections(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) ElectionStatus status,
            @RequestParam(required = false) ElectionType type
    ) {
        PagedResponseDTO<ElectionResponseDTO> response =
                electionService.listElections(page, size, status, type);
        return ResponseEntity.ok(response);
    }

    // Candidate endpoints

    @PostMapping("/elections/{electionId}/candidates")
    public ResponseEntity<CandidateResponseDTO> addCandidate(
            @PathVariable Long electionId,
            @Valid @RequestBody CandidateRequestDTO request) {
        request.setElectionId(electionId);
        CandidateResponseDTO response = candidateService.createCandidate(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/candidates/{id}")
    public ResponseEntity<CandidateResponseDTO> updateCandidate(
            @PathVariable Long id,
            @Valid @RequestBody CandidateRequestDTO request) {
        request.setId(id);
        CandidateResponseDTO response = candidateService.updateCandidate(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/candidates/{id}")
    public ResponseEntity<Void> deleteCandidate(@PathVariable Long id) {
        candidateService.deleteCandidate(id);
        return ResponseEntity.noContent().build();
    }

    // Vote endpoints

    @PostMapping("/votes")
    public ResponseEntity<VoteResponseDTO> castVote(
            @Valid @RequestBody VoteRequestDTO request) {
        VoteResponseDTO response = voteService.castVote(request);
        return ResponseEntity.ok(response);
    }

    // Results endpoint

    @GetMapping("/elections/{electionId}/results")
    public ResponseEntity<ElectionResultsDTO> getResults(@PathVariable Long electionId) {
        ElectionResultsDTO response = electionService.getResults(electionId);
        return ResponseEntity.ok(response);
    }
}

