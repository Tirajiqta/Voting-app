package com.tu.votingapp.entities.elections;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "parties")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "party_name", nullable = false, unique = true)
    private String name;

    @Column(name = "abbreviation")
    private String abbreviation;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "leader_name")
    private String leaderName;

    @ManyToOne
    @JoinColumn(name = "election_id")
    private ElectionEntity election;

    @OneToMany(mappedBy = "party", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CandidateEntity> candidates = new ArrayList<>();

    public PartyEntity(Long id) {
        this.id = id;
    }
}
