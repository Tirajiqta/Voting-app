package com.tu.votingapp.repositories.interfaces.referendum;

import com.tu.votingapp.entities.referendum.ReferendumEntity;
import com.tu.votingapp.entities.referendum.ReferendumOptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReferendumOptionRepository extends JpaRepository<ReferendumOptionEntity, Long> {

    /**
     * Fetch all options for a given referendum.
     */
    List<ReferendumOptionEntity> findByReferendum(ReferendumEntity referendum);

    /**
     * Fetch all options by referendum ID.
     */
    List<ReferendumOptionEntity> findByReferendum_Id(Long referendumId);

    /**
     * Find a specific option by referendum ID and option ID.
     */
    Optional<ReferendumOptionEntity> findByReferendum_IdAndId(Long referendumId, Long optionId);

    /**
     * Check if an option text already exists in a given referendum (for uniqueness).
     */
    boolean existsByReferendumAndOptionText(ReferendumEntity referendum, String optionText);

    /**
     * Count how many options a referendum currently has (to enforce min/max limits).
     */
    long countByReferendum(ReferendumEntity referendum);
}
