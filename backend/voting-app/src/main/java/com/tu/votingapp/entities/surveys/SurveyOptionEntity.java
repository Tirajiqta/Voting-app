package com.tu.votingapp.entities.surveys;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "survey_options")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurveyOptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "option_text")
    private String optionText;

    @Column(name = "vote_count")
    private int voteCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private SurveyQuestionsEntity question;
}
