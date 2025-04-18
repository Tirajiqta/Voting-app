package com.tu.votingapp.entities.referendum;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "referendum_votes",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"user_id", "referendum_id"}
        )
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReferendumVoteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Voter's ID from the UserPrincipal.
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * Referendum in which this vote was cast.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referendum_id", nullable = false)
    private ReferendumEntity referendum;

    /**
     * Option chosen by the voter.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", nullable = false)
    private ReferendumOptionEntity option;

    /**
     * Timestamp when the vote was cast.
     */
    @Column(name = "vote_timestamp", nullable = false)
    private LocalDateTime voteTimestamp;
}
