package com.tu.votingapp.entities.referendum;

import com.tu.votingapp.entities.UserEntity;
import com.tu.votingapp.enums.ReferendumStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "referendums")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ReferendumEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "referendum_title")
    private String title;

    @Column(name = "referendum_description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "referendum_question")
    private String question;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "referendum_status")
    private ReferendumStatus status;

    @OneToMany(mappedBy = "referendum", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReferendumOptionEntity> options = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private UserEntity createdBy;
}