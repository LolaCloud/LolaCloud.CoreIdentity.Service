import { Nullable } from "src/domain/utils";
import { Operator } from "src/infrastructure/database/typeORM/entities/operator.entity";
import { Permission } from "src/infrastructure/database/typeORM/entities/permission.entity";
import { DeepPartial } from "typeorm";

export abstract class OperatorRepository {

    abstract save(operator: Operator): Promise<Operator>;
    abstract create(operator: DeepPartial<Operator>): Promise<Operator>;
    abstract seeder(): Promise<void>
    abstract findByUsername(username: string): Promise<Nullable<Operator>>;
    abstract deleteById(operatorId: string): Promise<void>
    abstract HARD_deleteById(operatorId: string): Promise<void>
    abstract findById(operatorId: string): Promise<Nullable<Operator>>;
    abstract getPermissions(operatorId: string): Promise<Permission[]>
    abstract updateLastTimeActive(operatorId: string): Promise<void>;
    abstract findAll(): Promise<Operator[]>;

}