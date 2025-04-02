package com.tu.votingapp.repositories.interfaces.elections;

import com.tu.votingapp.entities.elections.PartyVoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartyVoteRepository extends JpaRepository<PartyVoteEntity, Long> {
}
