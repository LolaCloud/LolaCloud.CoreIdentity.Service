package com.miranda.luna.lci.infrastructure.persistence.adapter;

import com.miranda.luna.lci.domain.model.Operator;
import com.miranda.luna.lci.domain.repository.OperatorRepositoryPort;
import com.miranda.luna.lci.infrastructure.persistence.entity.OperatorJpaEntity;
import com.miranda.luna.lci.infrastructure.persistence.repository.SpringDataOperatorRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class OperatorRepositoryAdapter implements OperatorRepositoryPort {

    private final SpringDataOperatorRepository repository;

    public OperatorRepositoryAdapter(SpringDataOperatorRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Operator> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Operator> findByUsername(String username) {
        return repository.findByUsername(username).map(this::toDomain);
    }

    @Override
    public Operator save(Operator operator) {
        OperatorJpaEntity entity = toEntity(operator);
        OperatorJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<Operator> findAll() {
        return repository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    private Operator toDomain(OperatorJpaEntity entity) {
        return new Operator(
                entity.getId(),
                entity.getUsername(),
                entity.getPassword(),
                entity.getPermissions(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private OperatorJpaEntity toEntity(Operator domain) {
        return new OperatorJpaEntity(
                domain.getId(),
                domain.getUsername(),
                domain.getPassword(),
                domain.getPermissions(),
                domain.getStatus(),
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );
    }
}
