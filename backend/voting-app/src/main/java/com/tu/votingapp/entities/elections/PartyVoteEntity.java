package com.tu.votingapp.entities.elections;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "party_votes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartyVoteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "election_id")
    private ElectionEntity election;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id")
    private PartyEntity party;

    @Column(name = "vote_count")
    private int voteCount;

    public PartyVoteEntity(Long id) {
        this.id = id;
    }
}
