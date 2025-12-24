import { Injectable, NotFoundException } from "@nestjs/common";
import { OperatorRepository } from "src/application/gateways/operator-repository.gateway";
import { PermissionRepository } from "src/application/gateways/permission-repository.gateway";
import { OperatorUpdateDTO } from "src/domain/dtos/operator/update.dto";
import { Permission } from "src/infrastructure/database/typeORM/entities/permission.entity";
import { InvalidRequestException } from "src/infrastructure/http/exceptions/auth/invalid-request.exception";

@Injectable()
export class UpdateOperatorUseCase {

    constructor(
        private readonly repository: OperatorRepository,
        private readonly permissionRepository: PermissionRepository
    ) { }

    async execute(operatorId: string, payload: OperatorUpdateDTO) {

        const operator = await this.repository.findById(operatorId)

        if (!operator) {
            throw new NotFoundException()
        }

        if (!payload.name || payload.name?.length == 0) {
            throw new InvalidRequestException({ message: 'Name is required' });
        }

        operator.name = payload.name;
        operator.email = payload.email;

        if (operator.username != 'root') {
            const selectedPermissions: Permission[] = []

            for (const permission of payload.permissions) {
                const entity = await this.permissionRepository.findByPermission(permission)
                selectedPermissions.push(entity)
            }

            operator.permissions = selectedPermissions
        }

        await this.repository.save(operator)

        return {
            id: operator.id,
            username: operator.username,
            name: operator.name,
            created_at: operator.createdAt,
            email: operator.email,
            permissions: operator.permissions
        }

    }

}