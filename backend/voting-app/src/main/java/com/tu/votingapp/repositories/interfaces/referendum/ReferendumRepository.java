package com.tu.votingapp.repositories.interfaces.referendum;

import com.tu.votingapp.entities.referendum.ReferendumEntity;
import com.tu.votingapp.enums.ReferendumStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNullApi;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferendumRepository extends JpaRepository<ReferendumEntity, Long> {

    /**
     * Retrieve referendums by status with pagination.
     */
    Page<ReferendumEntity> findByStatus(ReferendumStatus status, Pageable pageable);

    /**
     * Retrieve all referendums (paginated).
     */
    @Override
    Page<ReferendumEntity> findAll(Pageable pageable);
}
