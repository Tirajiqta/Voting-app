package com.tu.votingapp.repositories.interfaces;

import com.tu.votingapp.entities.LocationRegionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRegionRepository extends JpaRepository<LocationRegionEntity, Long> {
}
