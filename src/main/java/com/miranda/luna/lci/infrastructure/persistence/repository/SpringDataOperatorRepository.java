package com.miranda.luna.lci.infrastructure.persistence.repository;

import com.miranda.luna.lci.infrastructure.persistence.entity.OperatorJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataOperatorRepository extends JpaRepository<OperatorJpaEntity, UUID> {
    Optional<OperatorJpaEntity> findByUsername(String username);
    boolean existsByUsername(String username);
}
