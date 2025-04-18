package com.tu.votingapp.services.impl.elections;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tu.votingapp.dto.request.elections.VoteRequestDTO;
import com.tu.votingapp.dto.response.elections.VoteResponseDTO;
import com.tu.votingapp.entities.elections.*;
import com.tu.votingapp.enums.ElectionStatus;
import com.tu.votingapp.repositories.interfaces.elections.*;
import com.tu.votingapp.services.interfaces.elections.VoteService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class VoteServiceImpl implements VoteService {

    private final VoteRepository voteRepository;
    private final ElectionRepository electionRepository;
    private final CandidateRepository candidateRepository;
    private final PartyRepository partyRepository;
    private final PartyVoteRepository partyVoteRepository;
    private final KafkaTemplate<String,String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final Logger logger = Logger.getLogger(VoteServiceImpl.class.getName());

    @Override
    @Transactional
    public VoteResponseDTO castVote(VoteRequestDTO request) {
        // Fetch election and validate status
        ElectionEntity election = electionRepository.findById(request.getElectionId())
                .orElseThrow(() -> new RuntimeException("Election not found"));
        if (election.getStatus() != ElectionStatus.ONGOING) {
            throw new IllegalStateException("Election is not open for voting");
        }
        // Enforce one vote per user per election
        Long userId = getCurrentUserId();
        if (voteRepository.existsByUserIdAndElection_Id(userId, election.getId())) {
            throw new IllegalStateException("User has already voted in this election");
        }
        // Create vote record
        VoteEntity vote = new VoteEntity();
        vote.setUserId(userId);
        vote.setElection(election);
        vote.setVoteTimestamp(new Date(System.currentTimeMillis()));
        // Tally candidate or party votes
        if (request.getCandidateId() != null) {
            CandidateEntity cand = candidateRepository.findById(request.getCandidateId())
                    .orElseThrow(() -> new RuntimeException("Candidate not found"));
            if (!cand.getElection().getId().equals(election.getId())) {
                throw new IllegalArgumentException("Candidate does not belong to election");
            }
            cand.setVotesCount(cand.getVotesCount() + 1);
            candidateRepository.save(cand);
        } else {
            // party vote
            PartyEntity party = partyRepository.findById(request.getPartyId())
                    .orElseThrow(() -> new RuntimeException("Party not found"));
            // increment or create PartyVoteEntity
            PartyVoteEntity pv = partyVoteRepository.findByElectionAndParty(election, party)
                    .orElse(new PartyVoteEntity(null, election, party, 0));
            pv.setVoteCount(pv.getVoteCount() + 1);
            partyVoteRepository.save(pv);
        }
        VoteEntity saved = voteRepository.save(vote);

        VoteEvent event = new VoteEvent(
                saved.getElection().getId(),
                request.getCandidateId(),
                request.getPartyId(),
                System.currentTimeMillis()
        );

        String payload = null;
        try {
            payload = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            logger.severe(e.getMessage());
        }
        kafkaTemplate.send("votes", payload);

        return new VoteResponseDTO(
                saved.getId(),
                saved.getUserId(),
                saved.getElection().getId(),
                request.getCandidateId(),
                request.getPartyId(),
                saved.getVoteTimestamp().toLocalDate().atStartOfDay() // Convert Date to LocalDateTime
        );
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ((com.tu.votingapp.security.UserPrincipal) auth.getPrincipal()).getId();
    }

    private static class VoteEvent {
        public final Long electionId;
        public final Long candidateId;
        public final Long partyId;
        public final long timestamp;
        public VoteEvent(Long electionId, Long candidateId, Long partyId, long timestamp) {
            this.electionId = electionId;
            this.candidateId = candidateId;
            this.partyId = partyId;
            this.timestamp = timestamp;
        }
    }
}
