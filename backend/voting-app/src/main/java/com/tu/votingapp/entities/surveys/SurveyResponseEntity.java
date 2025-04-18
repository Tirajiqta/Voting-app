package com.tu.votingapp.entities.surveys;

import com.tu.votingapp.entities.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "survey_responses",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "question_id"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurveyResponseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Who answered */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    /** Which survey question */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private SurveyQuestionsEntity question;

    /** Which option they picked */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", nullable = false)
    private SurveyOptionEntity option;

    /** When they submitted their answer */
    @Column(name = "responded_at", nullable = false)
    private LocalDateTime respondedAt;
}
