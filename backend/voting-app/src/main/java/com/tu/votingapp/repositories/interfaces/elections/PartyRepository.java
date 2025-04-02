package com.tu.votingapp.repositories.interfaces.elections;

import com.tu.votingapp.entities.elections.PartyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartyRepository extends JpaRepository<PartyEntity, Long> {
}
