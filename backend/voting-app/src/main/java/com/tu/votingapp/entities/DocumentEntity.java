package com.tu.votingapp.entities;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;

@Entity(name = "documents")
@Table(name = "documents")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString()
public class DocumentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @NonNull
    @Column(name = "document_number", nullable = false, unique = true, updatable = false)
    private String number;

    @NonNull
    @Column(name = "valid_from", nullable = false, updatable = false)
    private Date validFrom;

    @NonNull
    @Column(name = "valid_to", nullable = false, updatable = false)
    private Date validTo;

    @NonNull
    @Column(name = "document_issuer", nullable = false, updatable = false)
    private String issuer;

    @NonNull
    @Column(name = "gender", nullable = false, updatable = false)
    private Integer gender;

    @NonNull
    @Column(name = "date_of_birth", nullable = false, updatable = false)
    private Date dateOfBirth;

    @NonNull
    @Column(name = "permanent_address", nullable = false, updatable = false)
    private String permanentAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

}
