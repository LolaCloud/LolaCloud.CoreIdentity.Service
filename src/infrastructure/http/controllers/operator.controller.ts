import { Body, Controller, Delete, Post } from "@nestjs/common";
import { OperatorCreateUseCase } from "src/application/usecases/operator/create-operator.usecase";
import { OperatorDeleteUseCase } from "src/application/usecases/operator/delete-operator.usecase";

@Controller('/v1/operator')
export class OperatorController {

    constructor(
        private readonly createOperatorUseCase: OperatorCreateUseCase,
        private readonly deleteOperatorUseCase: OperatorDeleteUseCase
    ){}

    @Post()
    async create(@Body() body) {
        return this.createOperatorUseCase.execute(body)
    }
    
    @Delete()
    async delete(@Body() body) {
        return this.deleteOperatorUseCase.execute(body.operatorId)
    }

} 