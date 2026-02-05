import { MiddlewareConsumer, Module, NestModule } from "@nestjs/common";
import { HealthController } from "./controllers/health.controller";
import { DatabaseModule } from "../database/database.module";
import { AuthSignInUseCase } from "src/application/usecases/auth/sign-in.usecase";
import { SeederService } from "src/services/seeder.service";
import { AuthController } from "./controllers/auth.controller";
import { CryptService } from "src/services/crypt.service";
import { TokenService } from "src/services/token.service";
import { OperatorMiddleware } from "./middlewares/operator.middleware";
import { AuthorizationMiddleware } from "./middlewares/authorization.middleware";
import { OperatorCreateUseCase } from "src/application/usecases/operator/create-operator.usecase";
import { OperatorController } from "./controllers/operator.controller";
import { OperatorDeleteUseCase } from "src/application/usecases/operator/delete-operator.usecase";
import { GetAllOperatorsUseCase } from "src/application/usecases/operator/get-all-operator.usecase";
import { OperatorByIdUseCase } from "src/application/usecases/operator/operator-by-id.usecase";
import { UpdateOperatorUseCase } from "src/application/usecases/operator/update-operator.usecase";
import { UpdateOperatorPasswordUseCase } from "src/application/usecases/auth/update-password.usecase";
import { GetSessionsUseCase } from "src/application/usecases/auth/get-sessions.usecase";
import { DisableSessionUseCase } from "src/application/usecases/auth/disable-session.usecase";
import { RunwayService } from "src/services/runway/runway.service";
import { GetAllCloudflareAccountsUseCase } from "src/application/usecases/runway/get-all-accounts.usecase";
import { RunwayController } from "./controllers/runway.controller";
import { VerifyCloudflareTokenUseCase } from "src/application/usecases/runway/verify-cloudflare-token.usecase";
import { GetZonesFromCloudflareUseCase } from "src/application/usecases/runway/get-zones-from-cloudflare.usecase";
import { CreateCloudflareAccountUseCase } from "src/application/usecases/runway/create-cloudflare-account.usecase";
import { AddZoneUseCase } from "src/application/usecases/runway/add-zone.usecase";
import { GetZonesUseCase } from "src/application/usecases/runway/get-all-zones.usecase";
import { GetDNSRecordsByManagedZoneIdUseCase } from "src/application/usecases/runway/get-dns-records-by-managed-zone-id.usecase";
import { AddDNSRecordUseCase } from "src/application/usecases/runway/add-dns-record.usecase";
import { UpdateDNSRecordUseCase } from "src/application/usecases/runway/update-dns-record.usecase";
import { DeleteDNSRecordUseCase } from "src/application/usecases/runway/delete-dns-record.usecase";
import { RemoveZoneUseCase } from "src/application/usecases/runway/remove-zone-record.usecase";


const SERVICES = [
    CryptService,
    TokenService,
    SeederService,
]

const LOLA_SERVICES = [
    RunwayService
]

const USE_CASES = [
    AuthSignInUseCase,
    OperatorCreateUseCase,
    OperatorDeleteUseCase,
    GetAllOperatorsUseCase,
    OperatorByIdUseCase,
    UpdateOperatorUseCase,
    UpdateOperatorPasswordUseCase,
    GetSessionsUseCase,
    DisableSessionUseCase,
    GetAllCloudflareAccountsUseCase,
    VerifyCloudflareTokenUseCase,
    GetZonesFromCloudflareUseCase,
    CreateCloudflareAccountUseCase,
    AddZoneUseCase,
    GetZonesUseCase,
    GetDNSRecordsByManagedZoneIdUseCase,
    AddDNSRecordUseCase,
    UpdateDNSRecordUseCase,
    DeleteDNSRecordUseCase,
    RemoveZoneUseCase
]

const CONTROLLERS = [
        HealthController,
        AuthController,
        OperatorController,
        RunwayController
]

@Module({
    imports: [DatabaseModule],
    providers: [
        ...SERVICES,
        ...LOLA_SERVICES,
        ...USE_CASES,
    ],
    controllers: CONTROLLERS
})

export class HttpModule implements NestModule {

    configure(consumer: MiddlewareConsumer) {
        const middlewares = [AuthorizationMiddleware, OperatorMiddleware]
        for (const middleware of middlewares) {
            consumer.apply(middleware)
                .exclude('/v1/health')
                .exclude('/v1/auth/sign-in')
                .forRoutes('*')
        }
    }

}
