import { Injectable } from "@nestjs/common";
import { OperatorRepository } from "src/application/gateways/operator-repository.gateway";
import { SessionRepository } from "src/application/gateways/session-repository.gateway";
import { Nullable } from "src/domain/utils";
import { Operator } from "src/infrastructure/database/typeORM/entities/operator.entity";
import { InvalidRequestException } from "src/infrastructure/http/exceptions/auth/invalid-request.exception";
import { OperatorNotFoundException } from "src/infrastructure/http/exceptions/auth/operator-not-found.exception";

@Injectable()
export class OperatorDeleteUseCase {

    constructor(
        private readonly repository: OperatorRepository,
        private readonly sessionRepository: SessionRepository
    ) { }

    async execute(operatorId: string) {

        if (!operatorId) {
            throw new InvalidRequestException({ message: 'OperatorId is required' })
        }

        let operator: Nullable<Operator>

        try {
            operator = await this.repository.findById(operatorId);
        } catch (error) {
            throw new InvalidRequestException({ message: 'Invalid operatorId' })
        }

        if (!operator) {
            throw new OperatorNotFoundException()
        }

        if (operator.username === 'root') {
            throw new InvalidRequestException({ message: 'Operator root cant be deleted' })
        }


        await this.sessionRepository.disableSessionsByOperatorId(operatorId)
        await this.repository.deleteById(operatorId);

        return {
            message: 'Deleted'
        }
    }

}