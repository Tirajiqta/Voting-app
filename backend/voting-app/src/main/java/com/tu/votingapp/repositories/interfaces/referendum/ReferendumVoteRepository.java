package com.tu.votingapp.repositories.interfaces.referendum;

import com.tu.votingapp.entities.referendum.ReferendumEntity;
import com.tu.votingapp.entities.referendum.ReferendumVoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReferendumVoteRepository extends JpaRepository<ReferendumVoteEntity, Long> {

    /**
     * Check if a user has already voted in a given referendum.
     */
    boolean existsByUserIdAndReferendum_Id(Long userId, Long referendumId);

    /**
     * Fetch all votes for tallying results in a referendum.
     */
    List<ReferendumVoteEntity> findByReferendum(ReferendumEntity referendum);
}
