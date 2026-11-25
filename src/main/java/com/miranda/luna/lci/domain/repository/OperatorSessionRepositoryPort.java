package com.miranda.luna.lci.domain.repository;

import com.miranda.luna.lci.domain.model.OperatorSession;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OperatorSessionRepositoryPort {
    OperatorSession save(OperatorSession session);
    Optional<OperatorSession> findById(UUID id);
    Optional<OperatorSession> findByIdAndOperatorId(UUID id, UUID operatorId);
    List<OperatorSession> findActiveByOperatorId(UUID operatorId);
    List<OperatorSession> findAllByOperatorId(UUID operatorId);
}
