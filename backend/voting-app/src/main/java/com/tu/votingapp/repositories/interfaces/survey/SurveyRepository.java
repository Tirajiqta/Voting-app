package com.tu.votingapp.repositories.interfaces.survey;

import com.tu.votingapp.entities.surveys.SurveyEntity;
import com.tu.votingapp.enums.SurveyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SurveyRepository extends JpaRepository<SurveyEntity, Long> {

    /**
     * Retrieve surveys by status with pagination.
     */
    Page<SurveyEntity> findByStatus(SurveyStatus status, Pageable pageable);

    /**
     * Retrieve all surveys (paginated).
     */
    @Override
    Page<SurveyEntity> findAll(Pageable pageable);
}
