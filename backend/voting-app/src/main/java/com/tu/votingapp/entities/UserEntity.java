package com.tu.votingapp.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity(name = "user")
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user", schema = "voting")
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NonNull
    @Column(name = "name", nullable = false)
    private String name;

    @NonNull
    @Column(name = "email", nullable = false)
    private String email;

    @NonNull
    @Column(name = "passwordHash", nullable = false)
    private String password;

    @NonNull
    @Column(name = "phone", nullable = false)
    private String phone;

    @NonNull
    @Column(name = "current_address", nullable = false)
    private String currentAddress;

    @NonNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", referencedColumnName = "id", nullable = false)
    private RegionEntity regionId;

    @NonNull
    @Column(name = "egn", nullable = false)
    private String egn;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "document_id", referencedColumnName = "id")
    private List<DocumentEntity> documents;

}
