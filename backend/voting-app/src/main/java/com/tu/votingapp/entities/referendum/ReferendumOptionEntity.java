package com.tu.votingapp.entities.referendum;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "referendum_options")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReferendumOptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "option_text")
    private String optionText;

    @Column(name = "vote_count")
    private int voteCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referendum_id")
    private ReferendumEntity referendum;

    public ReferendumOptionEntity(Long id) {
        this.id = id;
    }
}
