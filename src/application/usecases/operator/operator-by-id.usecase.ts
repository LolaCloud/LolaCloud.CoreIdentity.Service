import { Injectable, NotFoundException } from "@nestjs/common";
import { OperatorRepository } from "src/application/gateways/operator-repository.gateway";

@Injectable()
export class OperatorByIdUseCase {

    constructor(
        private readonly repository: OperatorRepository
    ) {
    }

    async execute(id: string) {
        const operator = await this.repository.findById(id);

        if (!operator) {
            throw new NotFoundException()
        }

        return {
            operator: {
                id: operator.id,
                username: operator.username,
                name: operator.name,
                email: operator.email,
                created_at: operator.createdAt,
                updated_at: operator.updatedAt,
                permissions: operator.permissions,
                active: operator.deletedAt == null
            }
        }
    }

}