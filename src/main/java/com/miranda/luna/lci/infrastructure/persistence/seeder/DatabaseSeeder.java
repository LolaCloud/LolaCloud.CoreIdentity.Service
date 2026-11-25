package com.miranda.luna.lci.infrastructure.persistence.seeder;

import com.miranda.luna.lci.domain.model.Operator;
import com.miranda.luna.lci.domain.repository.OperatorRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);

    private final OperatorRepositoryPort operatorRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseSeeder(OperatorRepositoryPort operatorRepository, PasswordEncoder passwordEncoder) {
        this.operatorRepository = operatorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        final String rootUsername = "root";

        if (!operatorRepository.existsByUsername(rootUsername)) {
            logger.info("[LCI SEEDER] Root operator not found. Initializing root operator...");
            String encodedPassword = passwordEncoder.encode("toor");
            Operator rootOperator = Operator.create(
                    rootUsername,
                    encodedPassword,
                    List.of("*::*::*")
            );
            operatorRepository.save(rootOperator);
            logger.info("[LCI SEEDER] Root operator created successfully with unrestricted wildcard permissions (*::*::*).");
        } else {
            logger.info("[LCI SEEDER] Root operator already exists. Verifying default credentials...");
            Operator existing = operatorRepository.findByUsername(rootUsername).orElse(null);
            if (existing != null && !passwordEncoder.matches("toor", existing.getPassword())) {
                Operator updated = new Operator(
                        existing.getId(),
                        existing.getUsername(),
                        passwordEncoder.encode("toor"),
                        existing.getPermissions().contains("*::*::*") ? existing.getPermissions() : List.of("*::*::*"),
                        existing.getStatus(),
                        existing.getCreatedAt(),
                        java.time.Instant.now()
                );
                operatorRepository.save(updated);
                logger.info("[LCI SEEDER] Root operator password restored to default.");
            }
        }
    }
}
