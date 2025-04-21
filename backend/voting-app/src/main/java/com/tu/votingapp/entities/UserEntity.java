package com.tu.votingapp.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode
@Data
@Entity(name = "user")
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user", schema = "voting")
public class UserEntity {

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
    @Column(name = "password_hash", nullable = false)
    private String password;

    @NonNull
    @Column(name = "phone", nullable = false)
    private String phone;

    @NonNull
    @Column(name = "current_address", nullable = false)
    private String currentAddress;

    @NonNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id", referencedColumnName = "id", nullable = false)
    private LocationEntity regionId;

    @NonNull
    @Column(name = "egn", nullable = false)
    private String egn;

    @OneToOne(
            cascade = CascadeType.ALL,    // Good: Saves/updates/deletes Document when User is saved/updated/deleted
            fetch = FetchType.EAGER,       // Good: Loads Document only when explicitly accessed
            optional = false              // Implied by nullable=false on @JoinColumn, but explicit is fine
    )
    @JoinColumn(
            name = "document_id",         // Correct: FK column in the 'user' table
            referencedColumnName = "id",  // Correct: PK column in the 'documents' table
            nullable = false,             // Correct: Enforces the NOT NULL constraint at JPA/DDL level
            unique = true                 // Correct: Enforces the 1-to-1 relationship at DB level (one user per document_id)
    )
    private DocumentEntity document;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<RoleEntity> roles = new ArrayList<>();

    public UserEntity(Long id) {
        this.id = id;
    }

}
