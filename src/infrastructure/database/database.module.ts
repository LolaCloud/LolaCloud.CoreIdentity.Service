import { Module } from "@nestjs/common";
import { TypeOrmModule } from "@nestjs/typeorm";
import { dataSource } from "./typeORM/data-source";
import { Operator } from "./typeORM/entities/operator.entity";
import { OperatorRepository } from "src/application/gateways/operator-repository.gateway";
import { TypeORMOperatorRepository } from "./typeORM/repositories/typeorm-operator.repository";
import { CryptService } from "src/services/crypt.service";
import { Permission } from "./typeORM/entities/permission.entity";
import { PermissionRepository } from "src/application/gateways/permission-repository.gateway";
import { TypeORMPermissionRepository } from "./typeORM/repositories/typeorm-permission.repository";
import { SessionRepository } from "src/application/gateways/session-repository.gateway";
import { TypeORMSessionRepository } from "./typeORM/repositories/typeorm-session.repository";
import { Session } from "./typeORM/entities/session.entity";

@Module({
    imports: [
        TypeOrmModule.forRoot(dataSource.options),
        TypeOrmModule.forFeature([
            Operator,
            Permission,
            Session
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
        },
        {
            provide: SessionRepository,
            useClass: TypeORMSessionRepository
        }
    ],
    exports: [OperatorRepository, PermissionRepository, SessionRepository]
})
export class DatabaseModule {
}