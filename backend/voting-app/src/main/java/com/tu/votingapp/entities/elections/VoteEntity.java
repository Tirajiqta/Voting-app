package com.tu.votingapp.entities.elections;

import com.tu.votingapp.entities.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Entity
@Table(name = "votes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "election_id"})
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) // A vote MUST belong to a user
    @JoinColumn(name = "user_id", nullable = false) // Foreign key column
    private UserEntity user; // Changed from Long userId

    @ManyToOne(fetch = FetchType.LAZY, optional = false) // A vote MUST belong to an election
    @JoinColumn(name = "election_id", nullable = false)
    private ElectionEntity election;

    // --- Added Fields for Vote Choice ---

    @ManyToOne(fetch = FetchType.LAZY, optional = true) // Vote might be for a party only, so candidate is optional
    @JoinColumn(name = "candidate_id", nullable = true) // Allow NULL if voting only for party
    private CandidateEntity candidate; // The specific candidate voted for (if applicable)

    @ManyToOne(fetch = FetchType.LAZY, optional = true) // Vote might be for an independent candidate, so party is optional
    @JoinColumn(name = "party_id", nullable = true) // Allow NULL if voting for independent candidate
    private PartyEntity party; // The party voted for (either directly or the party of the candidate)


    @Column(name = "vote_timestamp")
    private Date voteTimestamp;

    public VoteEntity(Long id) {
        this.id = id;
    }

}