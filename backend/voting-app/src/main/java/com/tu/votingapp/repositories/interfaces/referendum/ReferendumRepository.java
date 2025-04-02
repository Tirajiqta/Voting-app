package com.tu.votingapp.repositories.interfaces.referendum;

import com.tu.votingapp.entities.referendum.ReferendumEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferendumRepository extends JpaRepository<ReferendumEntity, Long> {
}
