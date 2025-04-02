package com.tu.votingapp.repositories.interfaces.survey;

import com.tu.votingapp.entities.surveys.SurveyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SurveyRepository extends JpaRepository<SurveyEntity, Long> {
}
