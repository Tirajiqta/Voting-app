package com.tu.votingapp.entities.elections;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "candidates")
@Data
@EqualsAndHashCode(callSuper = false)
@ToString()
@AllArgsConstructor
@NoArgsConstructor
public class CandidateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "candidate_names")
    private String name;

    @Column(name = "bio")
    private String bio;

    @ManyToOne
    @JoinColumn(name = "election_id")
    private ElectionEntity election;

    @Column(name = "votes_count")
    private int votesCount;

    @Column(name = "image_uri")
    private String imageUri;

    @Column(name = "position")
    private String position;
}
