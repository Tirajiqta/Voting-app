package com.tu.votingapp.repositories.interfaces;

import com.tu.votingapp.entities.PermissionEntity;
import com.tu.votingapp.repositories.interfaces.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends BaseRepository<PermissionEntity, Long> {

    Optional<PermissionEntity> findByName(String name);
    boolean existsByName(String name);

}

