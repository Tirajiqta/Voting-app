package com.tu.votingapp.repositories.interfaces.elections;

import com.tu.votingapp.entities.UserEntity;
import com.tu.votingapp.entities.elections.ElectionEntity;
import com.tu.votingapp.entities.elections.VoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<VoteEntity, Long> {
    boolean existsByUserIdAndElection_Id(Long userId, Long electionId);
    boolean existsByUserAndElection(UserEntity user, ElectionEntity election);

}
