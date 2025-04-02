package com.tu.votingapp.repositories.interfaces.survey;

import com.tu.votingapp.entities.surveys.SurveyOptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SurveyOptionRepository extends JpaRepository<SurveyOptionEntity, Long> {
}
