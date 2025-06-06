package com.tu.votingapp.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor
@NoArgsConstructor
public class RegionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NonNull
    @Column(name = "region_name", nullable = false)
    private String name;

    @Column(name = "population")
    private Integer population;

    public RegionEntity(Long id) {
        this.id = id;
    }
}
