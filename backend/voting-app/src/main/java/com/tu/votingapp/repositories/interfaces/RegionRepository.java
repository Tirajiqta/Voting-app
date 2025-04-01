package com.tu.votingapp.repositories.interfaces;

import com.tu.votingapp.entities.RegionEntity;
import com.tu.votingapp.repositories.interfaces.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegionRepository extends BaseRepository<RegionEntity, Long> {

    Optional<RegionEntity> findByName(String name);

    boolean existsByName(String name);
}