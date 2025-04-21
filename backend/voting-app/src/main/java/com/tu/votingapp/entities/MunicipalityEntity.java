package com.tu.votingapp.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class MunicipalityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "population")
    private Long population;

    @ManyToOne(fetch = FetchType.LAZY) // Changed from @OneToOne to @ManyToOne
    @JoinColumn(name = "region_id", referencedColumnName = "id")
    private RegionEntity region;

    public MunicipalityEntity(Long id) {
        this.id = id;
    }
}
