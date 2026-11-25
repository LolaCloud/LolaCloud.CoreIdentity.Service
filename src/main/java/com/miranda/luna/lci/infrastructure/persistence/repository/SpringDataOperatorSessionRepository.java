package com.miranda.luna.lci.infrastructure.persistence.repository;

import com.miranda.luna.lci.infrastructure.persistence.entity.OperatorSessionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataOperatorSessionRepository extends JpaRepository<OperatorSessionJpaEntity, UUID> {

    List<OperatorSessionJpaEntity> findByOperatorIdAndIsRevokedFalseOrderByCreatedAtDesc(UUID operatorId);

    List<OperatorSessionJpaEntity> findByOperatorIdOrderByCreatedAtDesc(UUID operatorId);

    Optional<OperatorSessionJpaEntity> findByIdAndOperatorId(UUID id, UUID operatorId);
}
