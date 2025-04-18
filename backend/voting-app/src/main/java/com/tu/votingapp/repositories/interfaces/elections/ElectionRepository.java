package com.tu.votingapp.repositories.interfaces.elections;

import com.tu.votingapp.entities.elections.ElectionEntity;
import com.tu.votingapp.enums.ElectionStatus;
import com.tu.votingapp.enums.ElectionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElectionRepository extends JpaRepository<ElectionEntity, Long> {
    Page<ElectionEntity> findByStatus(ElectionStatus status, Pageable pageable);

    /**
     * Find elections by type with pagination.
     */
    Page<ElectionEntity> findByElectionType(ElectionType electionType, Pageable pageable);

    /**
     * Find elections by status and type with pagination.
     */
    Page<ElectionEntity> findByStatusAndElectionType(
            ElectionStatus status,
            ElectionType electionType,
            Pageable pageable
    );
}
