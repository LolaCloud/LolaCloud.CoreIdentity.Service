import { Injectable } from "@nestjs/common";
import { OperatorRepository } from "src/application/gateways/operator-repository.gateway";

@Injectable()
export class GetAllOperatorsUseCase {

    constructor(
        private readonly repository: OperatorRepository
    ) {
    }

    async execute() {
        const operators = await this.repository.findAll();

        return {
            operators: operators.map(operator => ({
                id: operator.id,
                username: operator.username,
                name: operator.name,
                created_at: operator.createdAt
            }))
        }
    }

}