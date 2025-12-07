import { Module, OnModuleInit } from "@nestjs/common";
import { TypeOrmModule } from "@nestjs/typeorm";
import { dataSource } from "./typeORM/data-source";
import { Operator } from "./typeORM/entities/operator.entity";
import { OperatorRepository } from "src/application/gateways/operator-repository.gateway";
import { TypeORMOperatorRepository } from "./typeORM/repositories/typeorm-operator.repository";
import { CryptService } from "src/services/crypt.service";

@Module({
    imports: [
        TypeOrmModule.forRoot(dataSource.options),
        TypeOrmModule.forFeature([
            Operator
        ])
    ],
    providers: [
        CryptService,
        {
            provide: OperatorRepository,
            useClass: TypeORMOperatorRepository
        }
    ],
    exports: [OperatorRepository]
})
export class DatabaseModule{
}