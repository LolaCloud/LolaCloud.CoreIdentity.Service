package com.miranda.luna.lci.domain.repository;

import com.miranda.luna.lci.domain.model.Operator;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OperatorRepositoryPort {
    Optional<Operator> findById(UUID id);
    Optional<Operator> findByUsername(String username);
    List<Operator> findAll();
    Operator save(Operator operator);
    boolean existsByUsername(String username);
    void deleteById(UUID id);
}

