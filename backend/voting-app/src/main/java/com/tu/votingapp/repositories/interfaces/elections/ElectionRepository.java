package com.tu.votingapp.repositories.interfaces.elections;

import com.tu.votingapp.entities.elections.ElectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElectionRepository extends JpaRepository<ElectionEntity, Long> {
}
