package com.tu.votingapp.repositories.interfaces.elections;

import com.tu.votingapp.entities.elections.ElectionEntity;
import com.tu.votingapp.entities.elections.PartyVoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartyVoteRepository extends JpaRepository<PartyVoteEntity, Long> {
    /**
     * Retrieve all party vote records for the given election.
     */
    List<PartyVoteEntity> findByElection(ElectionEntity election);

    /**
     * Find the vote record for a specific party within an election, if it exists.
     */
    Optional<PartyVoteEntity> findByElectionAndParty(ElectionEntity election, com.tu.votingapp.entities.elections.PartyEntity party);
}
