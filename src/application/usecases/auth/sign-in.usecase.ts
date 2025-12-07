import { HttpException, HttpStatus, Injectable } from "@nestjs/common";
import { OperatorRepository } from "src/application/gateways/operator-repository.gateway";
import { AuthSignInDTO } from "src/domain/dtos/auth/sign-in.dto";
import { InvalidCredentialsException } from "src/infrastructure/http/exceptions/auth/invalid-credentials.exception";
import { CryptService } from "src/services/crypt.service";
import { TokenService } from "src/services/token.service";

@Injectable()
export class AuthSignInUseCase {

    constructor(
        private readonly repository: OperatorRepository,
        private readonly cryptService: CryptService,
        private readonly tokenService: TokenService
    ) {}

    async execute(payload: AuthSignInDTO) {
        const operator = await this.repository.findByUsername(payload.username);

        if (!operator) {
            throw new InvalidCredentialsException()
        }

        const samePassword = await this.cryptService.compare(payload.password, operator.password);

        if (!samePassword) {
             throw new InvalidCredentialsException()
        }

        const accessToken = await this.tokenService.generate({
            operatorId: operator.id
        })

        return {
            access_token: accessToken
        } 
    }

}