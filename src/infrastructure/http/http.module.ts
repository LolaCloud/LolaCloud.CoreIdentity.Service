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
import { AuthMeUseCase } from "src/application/usecases/auth/me.usecase";

@Module({
    imports: [DatabaseModule],
    providers: [
        CryptService,
        TokenService,
        SeederService,
        AuthSignInUseCase,
        AuthMeUseCase
    ],
    controllers: [
        HealthController,
        AuthController
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