package com.tu.votingapp.repositories.interfaces.referendum;

import com.tu.votingapp.entities.referendum.ReferendumOptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferendumOptionRepository extends JpaRepository<ReferendumOptionEntity, Long> {
}
