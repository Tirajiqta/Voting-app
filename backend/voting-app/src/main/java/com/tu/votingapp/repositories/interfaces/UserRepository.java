package com.tu.votingapp.repositories.interfaces;

import com.tu.votingapp.entities.UserEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends BaseRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEgn(String egn);

    Optional<UserEntity> findByEgn(String egn);
}