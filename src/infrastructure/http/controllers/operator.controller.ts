import { Body, Controller, Delete, Get, Post, UseGuards } from "@nestjs/common";
import { OperatorCreateUseCase } from "src/application/usecases/operator/create-operator.usecase";
import { OperatorDeleteUseCase } from "src/application/usecases/operator/delete-operator.usecase";
import { GetAllOperatorsUseCase } from "src/application/usecases/operator/get-all-operator.usecase";
import { LolaPermission } from "../decorators/permission.decorator";
import { LolaPermissions } from "src/domain/enums/permissions.enum";
import { PermissionsGuard } from "../guards/permission.guard";
import { Operator } from "../decorators/operator.decorator";

@Controller('/v1/operator')
@UseGuards(PermissionsGuard)
export class OperatorController {

    constructor(
        private readonly createOperatorUseCase: OperatorCreateUseCase,
        private readonly deleteOperatorUseCase: OperatorDeleteUseCase,
        private readonly getAllOperatorsUseCase: GetAllOperatorsUseCase
    ) { }

    @Post()
    @LolaPermission(LolaPermissions.LCI_OPERATOR_CREATE)
    async create(@Body() body) {
        return this.createOperatorUseCase.execute(body)
    }

    @Delete()
    @LolaPermission(LolaPermissions.LCI_OPERATOR_DELETE)
    async delete(@Body() body) {
        return this.deleteOperatorUseCase.execute(body.operatorId)
    }

    @Get()
    async getAll() {
        return this.getAllOperatorsUseCase.execute()
    }

    @Get('me')
    async me(@Operator() operator) {
        return {
            operator: {
                id: operator.id,
                username: operator.username,
                name: operator.name,
                email: operator.email,
                created_at: operator.createdAt,
                updated_at: operator.updatedAt,
                permissions: operator.permissions
            }
        }
    }

} 