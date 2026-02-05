import { EnvConfig } from "src/configuration";
import { DataSource } from "typeorm";
import { Operator } from "./entities/operator.entity";
import { Permission } from "./entities/permission.entity";
import { Session } from "./entities/session.entity";

export const dataSource = new DataSource({
    url: EnvConfig.DATABASE_URL,
    schema: 'lci',
    applicationName: 'LCI',
    type: 'postgres',
    entities: [Operator, Permission, Session],
    synchronize: false,
    migrationsRun: true,
    migrations: ['dist/src/infrastructure/database/typeORM/migrations/*{.ts,.js}'],
    // logging: true,
    ssl: false,
})