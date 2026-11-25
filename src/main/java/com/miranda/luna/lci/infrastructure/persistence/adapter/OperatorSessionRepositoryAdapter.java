package com.miranda.luna.lci.infrastructure.persistence.adapter;

import com.miranda.luna.lci.domain.model.OperatorSession;
import com.miranda.luna.lci.domain.repository.OperatorSessionRepositoryPort;
import com.miranda.luna.lci.infrastructure.persistence.entity.OperatorSessionJpaEntity;
import com.miranda.luna.lci.infrastructure.persistence.repository.SpringDataOperatorSessionRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class OperatorSessionRepositoryAdapter implements OperatorSessionRepositoryPort {

    private final SpringDataOperatorSessionRepository repository;

    public OperatorSessionRepositoryAdapter(SpringDataOperatorSessionRepository repository) {
        this.repository = repository;
    }

    @Override
    public OperatorSession save(OperatorSession session) {
        OperatorSessionJpaEntity entity = toEntity(session);
        OperatorSessionJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<OperatorSession> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<OperatorSession> findByIdAndOperatorId(UUID id, UUID operatorId) {
        return repository.findByIdAndOperatorId(id, operatorId).map(this::toDomain);
    }

    @Override
    public List<OperatorSession> findActiveByOperatorId(UUID operatorId) {
        return repository.findByOperatorIdAndIsRevokedFalseOrderByCreatedAtDesc(operatorId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<OperatorSession> findAllByOperatorId(UUID operatorId) {
        return repository.findByOperatorIdOrderByCreatedAtDesc(operatorId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private OperatorSession toDomain(OperatorSessionJpaEntity entity) {
        return new OperatorSession(
                entity.getId(),
                entity.getOperatorId(),
                entity.getIpAddress(),
                entity.getDeviceType(),
                entity.getBrowser(),
                entity.getOperatingSystem(),
                entity.getUserAgent(),
                entity.isRevoked(),
                entity.getCreatedAt(),
                entity.getLastActiveAt(),
                entity.getExpiresAt()
        );
    }

    private OperatorSessionJpaEntity toEntity(OperatorSession domain) {
        return new OperatorSessionJpaEntity(
                domain.getId(),
                domain.getOperatorId(),
                domain.getIpAddress(),
                domain.getDeviceType(),
                domain.getBrowser(),
                domain.getOperatingSystem(),
                domain.getUserAgent(),
                domain.isRevoked(),
                domain.getCreatedAt(),
                domain.getLastActiveAt(),
                domain.getExpiresAt()
        );
    }
}
