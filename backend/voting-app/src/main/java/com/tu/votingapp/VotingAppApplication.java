package com.tu.votingapp;

import com.tu.votingapp.repositories.impl.BaseRepositoryImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(
        basePackages = "com.tu.votingapp.repositories",
        repositoryBaseClass = BaseRepositoryImpl.class
)
public class VotingAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(VotingAppApplication.class, args);
    }

}
