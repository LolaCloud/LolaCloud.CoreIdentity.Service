import { Injectable } from "@nestjs/common";
import { OperatorRepository } from "src/application/gateways/operator-repository.gateway";
import { SessionRepository } from "src/application/gateways/session-repository.gateway";
import { AuthSignInDTO } from "src/domain/dtos/auth/sign-in.dto";
import { InvalidCredentialsException } from "src/infrastructure/http/exceptions/auth/invalid-credentials.exception";
import { InvalidRequestException } from "src/infrastructure/http/exceptions/auth/invalid-request.exception";
import { CryptService } from "src/services/crypt.service";
import { TokenService } from "src/services/token.service";

@Injectable()
export class AuthSignInUseCase {

    constructor(
        private readonly operatorRepository: OperatorRepository,
        private readonly sessionRepository: SessionRepository,
        private readonly cryptService: CryptService,
        private readonly tokenService: TokenService,
    ) {}

    async execute(payload: AuthSignInDTO, ip: string, userAgent: string) {

        if (!payload.password || payload.password?.length == 0) {
            throw new InvalidRequestException({message: 'Password is required'});
        }

         if (!payload.username || payload.username?.length == 0) {
            throw new InvalidRequestException({message: 'Username is required'});
        }

        const operator = await this.operatorRepository.findByUsername(payload.username);

        if (!operator) {
            throw new InvalidCredentialsException()
        }
        const samePassword = await this.cryptService.compare(payload.password, operator.password);

        if (!samePassword) {
             throw new InvalidCredentialsException()
        }

        const sessionId = crypto.randomUUID()

         await this.sessionRepository.create({
            tokenIdentifier: sessionId,
            operator,
            ipAddress: ip,
            userAgent,
            expiresAt: new Date(Date.now() + 1000 * 60 * 60 * 24 * 7), // week,
            lastActivityAt: new Date()
        })

        const accessToken = await this.tokenService.generate({
            sub: operator.id,
            sid: sessionId,
            username: operator.username
        })

        return {
            access_token: accessToken
        } 
    }

}