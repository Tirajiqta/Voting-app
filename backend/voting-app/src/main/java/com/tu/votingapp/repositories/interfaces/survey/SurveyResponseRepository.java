package com.tu.votingapp.repositories.interfaces.survey;

import com.tu.votingapp.entities.surveys.SurveyEntity;
import com.tu.votingapp.entities.surveys.SurveyResponseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyResponseRepository extends JpaRepository<SurveyResponseEntity, Long> {
    boolean existsByUser_IdAndQuestion_Id(Long userId, Long questionId);
    List<SurveyResponseEntity> findByQuestion_Survey(SurveyEntity survey);
}

