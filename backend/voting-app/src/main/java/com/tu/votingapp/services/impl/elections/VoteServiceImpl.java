package com.tu.votingapp.services.impl.elections;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tu.votingapp.dto.request.elections.VoteRequestDTO;
import com.tu.votingapp.dto.response.elections.VoteResponseDTO;
import com.tu.votingapp.entities.elections.CandidateEntity;
import com.tu.votingapp.entities.elections.ElectionEntity;
import com.tu.votingapp.entities.elections.PartyEntity;
import com.tu.votingapp.entities.elections.PartyVoteEntity;
import com.tu.votingapp.entities.elections.VoteEntity;
import com.tu.votingapp.enums.ElectionStatus;
import com.tu.votingapp.repositories.interfaces.elections.CandidateRepository;
import com.tu.votingapp.repositories.interfaces.elections.ElectionRepository;
import com.tu.votingapp.repositories.interfaces.elections.PartyRepository;
import com.tu.votingapp.repositories.interfaces.elections.PartyVoteRepository;
import com.tu.votingapp.repositories.interfaces.elections.VoteRepository;
import com.tu.votingapp.services.interfaces.elections.VoteService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class VoteServiceImpl implements VoteService {

    private final VoteRepository voteRepository;
    private final ElectionRepository electionRepository;
    private final CandidateRepository candidateRepository;
    private final PartyRepository partyRepository;
    private final PartyVoteRepository partyVoteRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final Logger logger = Logger.getLogger(VoteServiceImpl.class.getName());

    @Override
    @Transactional
    public VoteResponseDTO castVote(VoteRequestDTO request) {
        Long electionId = request.getElectionId();
        Long userId = getCurrentUserId();
        logger.info(() -> "Casting vote in electionId=" + electionId + ", userId=" + userId);

        // Validate election
        ElectionEntity election = electionRepository.findById(electionId)
                .orElseThrow(() -> new RuntimeException("Election not found: " + electionId));
        if (election.getStatus() != ElectionStatus.ONGOING) {
            logger.warning(() -> "Attempt to vote in non-ongoing electionId=" + electionId);
            throw new IllegalStateException("Election is not open for voting");
        }

        // One vote per user
        if (voteRepository.existsByUserIdAndElection_Id(userId, electionId)) {
            logger.warning(() -> "Duplicate vote attempt userId=" + userId + " electionId=" + electionId);
            throw new IllegalStateException("User has already voted in this election");
        }

        // Prepare vote record
        VoteEntity vote = new VoteEntity();
        vote.setUserId(userId);
        vote.setElection(election);
        vote.setVoteTimestamp(new Date(System.currentTimeMillis()));

        // Tally
        if (request.getCandidateId() != null) {
            Long candidateId = request.getCandidateId();
            logger.fine(() -> "Tallying candidate vote for candidateId=" + candidateId);
            CandidateEntity cand = candidateRepository.findById(candidateId)
                    .orElseThrow(() -> new RuntimeException("Candidate not found: " + candidateId));
            if (!cand.getElection().getId().equals(electionId)) {
                logger.warning(() -> "Candidate does not belong to election: candidateId=" + candidateId);
                throw new IllegalArgumentException("Candidate does not belong to election");
            }
            cand.setVotesCount(cand.getVotesCount() + 1);
            candidateRepository.save(cand);
        } else {
            Long partyId = request.getPartyId();
            logger.fine(() -> "Tallying party vote for partyId=" + partyId);
            PartyEntity party = partyRepository.findById(partyId)
                    .orElseThrow(() -> new RuntimeException("Party not found: " + partyId));
            PartyVoteEntity pv = partyVoteRepository.findByElectionAndParty(election, party)
                    .orElse(new PartyVoteEntity(null, election, party, 0));
            pv.setVoteCount(pv.getVoteCount() + 1);
            partyVoteRepository.save(pv);
        }

        VoteEntity saved = voteRepository.save(vote);
        logger.info(() -> "Vote saved id=" + saved.getId());

        // Publish event
        VoteEvent event = new VoteEvent(
                electionId,
                request.getCandidateId(),
                request.getPartyId(),
                System.currentTimeMillis()
        );
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("votes", payload);
            logger.fine(() -> "Published vote event for electionId=" + electionId);
        } catch (JsonProcessingException e) {
            logger.log(Level.SEVERE, "Failed to serialize VoteEvent", e);
        }

        return new VoteResponseDTO(
                saved.getId(),
                saved.getUserId(),
                saved.getElection().getId(),
                request.getCandidateId(),
                request.getPartyId(),
                saved.getVoteTimestamp().toLocalDate().atStartOfDay()
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
