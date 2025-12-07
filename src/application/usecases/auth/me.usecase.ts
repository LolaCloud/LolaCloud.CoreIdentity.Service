import { Injectable } from "@nestjs/common";
import { Operator } from "src/infrastructure/database/typeORM/entities/operator.entity";

@Injectable()
export class AuthMeUseCase {

    execute(operator: Operator) {
        return {
            ...operator,
            password: undefined
        }
    }

}