import { Module, OnModuleInit } from "@nestjs/common";
import { TypeOrmModule } from "@nestjs/typeorm";
import { dataSource } from "./typeORM/data-source";
import { Operator } from "./typeORM/entities/operator.entity";
import { OperatorRepository } from "src/application/gateways/operator-repository.gateway";
import { TypeORMOperatorRepository } from "./typeORM/repositories/typeorm-operator.repository";
import { CryptService } from "src/services/crypt.service";
import { Permission } from "./typeORM/entities/permission.entity";
import { PermissionRepository } from "src/application/gateways/permission-repository.gateway";
import { TypeORMPermissionRepository } from "./typeORM/repositories/typeorm-permission.repository";

@Module({
    imports: [
        TypeOrmModule.forRoot(dataSource.options),
        TypeOrmModule.forFeature([
            Operator,
            Permission
        ])
    ],
    providers: [
        CryptService,
        {
            provide: OperatorRepository,
            useClass: TypeORMOperatorRepository
        },
         {
            provide: PermissionRepository,
            useClass: TypeORMPermissionRepository
        }
    ],
    exports: [OperatorRepository, PermissionRepository]
})
export class DatabaseModule{
}