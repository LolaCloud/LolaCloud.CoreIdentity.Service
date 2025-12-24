import { Body, Controller, Delete, Get, Param, Patch, Post, UseGuards } from "@nestjs/common";
import { OperatorCreateUseCase } from "src/application/usecases/operator/create-operator.usecase";
import { OperatorDeleteUseCase } from "src/application/usecases/operator/delete-operator.usecase";
import { GetAllOperatorsUseCase } from "src/application/usecases/operator/get-all-operator.usecase";
import { LolaPermission } from "../decorators/permission.decorator";
import { LolaPermissions } from "src/domain/enums/permissions.enum";
import { PermissionsGuard } from "../guards/permission.guard";
import { Operator } from "../decorators/operator.decorator";
import { Operator as OperatorEntity } from "src/infrastructure/database/typeORM/entities/operator.entity";
import { OperatorByIdUseCase } from "src/application/usecases/operator/operator-by-id.usecase";
import { UpdateOperatorUseCase } from "src/application/usecases/operator/update-operator.usecase";

@Controller('/v1/operator')
@UseGuards(PermissionsGuard)
export class OperatorController {

    constructor(
        private readonly createOperatorUseCase: OperatorCreateUseCase,
        private readonly deleteOperatorUseCase: OperatorDeleteUseCase,
        private readonly getAllOperatorsUseCase: GetAllOperatorsUseCase,
        private readonly getOperatorByIdUseCase: OperatorByIdUseCase,
        private readonly updateOperatorUseCase: UpdateOperatorUseCase
    ) { }

    @Post()
    @LolaPermission(LolaPermissions.LCI_OPERATOR_CREATE)
    async create(@Body() body) {
        return this.createOperatorUseCase.execute(body)
    }

    @Delete(':operatorId')
    @LolaPermission(LolaPermissions.LCI_OPERATOR_DELETE)
    async delete(@Param() params) {
        return this.deleteOperatorUseCase.execute(params.operatorId)
    }

    @Get()
    async getAll() {
        return this.getAllOperatorsUseCase.execute()
    }

    @Get('me')
    async me(@Operator() operator: OperatorEntity) {

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

    @Get(':operatorId')
    async byId(@Param() params) {
        return this.getOperatorByIdUseCase.execute(params.operatorId)
    }

    @Patch(':operatorId')
    @LolaPermission(LolaPermissions.LCI_OPERATOR_UPDATE)
    async update(@Param() params, @Body() payload) {
        return this.updateOperatorUseCase.execute(params.operatorId, payload)
    }

} 