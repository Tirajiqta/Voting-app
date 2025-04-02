package com.tu.votingapp.repositories.interfaces;

import com.tu.votingapp.entities.MunicipalityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MunicipalityRepository extends JpaRepository<MunicipalityEntity, Long> {
}
