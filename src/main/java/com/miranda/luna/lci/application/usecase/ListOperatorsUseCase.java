package com.miranda.luna.lci.application.usecase;

import com.miranda.luna.lci.domain.repository.OperatorRepositoryPort;
import com.miranda.luna.lci.infrastructure.web.dto.OperatorResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListOperatorsUseCase {

    private final OperatorRepositoryPort operatorRepository;

    public ListOperatorsUseCase(OperatorRepositoryPort operatorRepository) {
        this.operatorRepository = operatorRepository;
    }

    public List<OperatorResponse> execute() {
        return operatorRepository.findAll().stream()
                .map(operator -> new OperatorResponse(
                        operator.getId(),
                        operator.getUsername(),
                        operator.getPermissions(),
                        operator.getStatus()
                ))
                .toList();
    }
}
