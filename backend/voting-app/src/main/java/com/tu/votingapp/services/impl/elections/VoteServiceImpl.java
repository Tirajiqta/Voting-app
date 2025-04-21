package com.tu.votingapp.services.impl.elections;

import com.tu.votingapp.dto.general.elections.VoteDTO;
import com.tu.votingapp.dto.request.elections.VoteRequestDTO;
import com.tu.votingapp.entities.UserEntity;
import com.tu.votingapp.entities.elections.CandidateEntity;
import com.tu.votingapp.entities.elections.ElectionEntity;
import com.tu.votingapp.entities.elections.PartyEntity;
import com.tu.votingapp.entities.elections.VoteEntity;
import com.tu.votingapp.enums.ElectionStatus;
import com.tu.votingapp.repositories.interfaces.UserRepository;
import com.tu.votingapp.repositories.interfaces.elections.CandidateRepository;
import com.tu.votingapp.repositories.interfaces.elections.ElectionRepository;
import com.tu.votingapp.repositories.interfaces.elections.PartyRepository;
import com.tu.votingapp.repositories.interfaces.elections.PartyVoteRepository;
import com.tu.votingapp.repositories.interfaces.elections.VoteRepository;
import com.tu.votingapp.services.interfaces.elections.VoteService;
import com.tu.votingapp.utils.mappers.election.VoteMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    private final Logger logger = Logger.getLogger(VoteServiceImpl.class.getName());
    private final UserRepository userRepository;
    private final VoteMapper voteMapper;

    @Override
    @Transactional
    public VoteDTO castVote(VoteRequestDTO decryptedVote) {
        Long authenticatedUserId = getCurrentUserId();
        logger.info(() -> "Processing vote cast request for user ID: " + authenticatedUserId + " in election ID: " + decryptedVote.getElectionId());

        // --- Validation ---
        // 1. Validate XOR condition for candidate/party
        boolean isCandidateVote = decryptedVote.getCandidateId() != null;
        boolean isPartyVote = decryptedVote.getPartyId() != null;
        if (isCandidateVote == isPartyVote) { // XOR check
            throw new IllegalArgumentException("Invalid vote: Must vote for exactly one of candidate or party.");
        }
        if (decryptedVote.getElectionId() == null) {
            throw new IllegalArgumentException("Invalid vote: Election ID is required.");
        }

        // 2. Fetch entities
        UserEntity user = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + authenticatedUserId));
        ElectionEntity election = electionRepository.findById(decryptedVote.getElectionId())
                .orElseThrow(() -> new EntityNotFoundException("Election not found: " + decryptedVote.getElectionId()));

        // 3. Check Election Status and Dates (Crucial!)
        // Example - add more detailed checks based on your ElectionStatus enum
        if (election.getStatus() != ElectionStatus.OPEN) {
            throw new IllegalStateException("Cannot cast vote: Election is not active. Status: " + election.getStatus());
        }
        LocalDateTime now = LocalDateTime.now();
        // Assuming your ElectionEntity uses java.time.LocalDate now
        if (now.toLocalDate().isBefore(election.getStartDate().toLocalDate()) || now.toLocalDate().isAfter(election.getEndDate().toLocalDate())) {
            throw new IllegalStateException("Cannot cast vote: Election is not within the voting period.");
        }


        // 4. Check if user already voted in this election (uses unique constraint)
        // The unique constraint on VoteEntity (user_id, election_id) will enforce this mostly,
        // but checking beforehand provides a clearer error.
        if (voteRepository.existsByUserAndElection(user, election)) { // Assumes existsByUserAndElection method in VoteRepository
            throw new DataIntegrityViolationException("User " + authenticatedUserId + " has already voted in election " + decryptedVote.getElectionId());
        }

        // 5. Fetch Candidate/Party if applicable and validate they belong to the election
        CandidateEntity candidate = null;
        PartyEntity party = null;

        if (isCandidateVote) {
            candidate = candidateRepository.findById(decryptedVote.getCandidateId())
                    .orElseThrow(() -> new EntityNotFoundException("Candidate not found: " + decryptedVote.getCandidateId()));
            // Validate candidate belongs to the election
            if (!candidate.getElection().getId().equals(election.getId())) {
                throw new IllegalArgumentException("Invalid vote: Candidate " + candidate.getId() + " does not belong to election " + election.getId());
            }
            // Optionally assign the candidate's party if storing party for candidate votes
            party = candidate.getParty(); // May be null if independent
        } else { // isPartyVote must be true due to XOR check
            party = partyRepository.findById(decryptedVote.getPartyId())
                    .orElseThrow(() -> new EntityNotFoundException("Party not found: " + decryptedVote.getPartyId()));
            // Optional: Validate party is actually participating in this election
            // This requires the ManyToMany relationship setup correctly
            // if (!election.getParties().contains(party)) { // Requires election.getParties() to be loaded
            //     throw new IllegalArgumentException("Invalid vote: Party " + party.getId() + " is not participating in election " + election.getId());
            // }
        }


        // --- Create and Save Vote ---
        VoteEntity voteEntity = new VoteEntity();
        voteEntity.setUser(user);
        voteEntity.setElection(election);
        voteEntity.setCandidate(candidate); // Will be null if party vote
        voteEntity.setParty(party);         // Will be null if independent candidate vote / party wasn't found for candidate
        // voteTimestamp is handled by @CreationTimestamp

        try {
            VoteEntity savedVote = voteRepository.save(voteEntity);
            logger.info(() -> "Vote successfully recorded with ID: " + savedVote.getId());
            return voteMapper.toDto(savedVote); // Map entity to response DTO
        } catch (DataIntegrityViolationException e) {
            // Catch potential unique constraint violation again (race condition defense)
            logger.warning("Vote casting failed for user " + authenticatedUserId + " in election " + election.getId() + " due to constraint violation (likely duplicate vote).");
            throw new DataIntegrityViolationException("Vote could not be recorded. You might have already voted.", e);
        }
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
