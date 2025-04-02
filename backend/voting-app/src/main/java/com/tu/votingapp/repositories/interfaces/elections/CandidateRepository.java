package com.tu.votingapp.repositories.interfaces.elections;

import com.tu.votingapp.entities.elections.CandidateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CandidateRepository extends JpaRepository<CandidateEntity, Long> {
}
