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

@Module({
    imports: [DatabaseModule],
    providers: [
        CryptService,
        TokenService,
        SeederService,
        AuthSignInUseCase,
        OperatorCreateUseCase,
        OperatorDeleteUseCase,
        GetAllOperatorsUseCase,
        OperatorByIdUseCase,
        UpdateOperatorUseCase,
        UpdateOperatorPasswordUseCase,
        GetSessionsUseCase,
        DisableSessionUseCase
    ],
    controllers: [
        HealthController,
        AuthController,
        OperatorController
    ]
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