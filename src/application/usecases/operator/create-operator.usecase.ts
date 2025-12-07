import { Injectable } from "@nestjs/common";
import { OperatorRepository } from "src/application/gateways/operator-repository.gateway";
import { OperatorCreateDTO } from "src/domain/dtos/operator/create.dto";
import { InvalidRequestException } from "src/infrastructure/http/exceptions/auth/invalid-request.exception";
import { CryptService } from "src/services/crypt.service";

@Injectable()
export class OperatorCreateUseCase {

    constructor(
        private readonly repository: OperatorRepository,
        private readonly cryptService: CryptService
    ) { }

    async execute(payload: OperatorCreateDTO) {

        if (!payload.password || payload.password?.length == 0) {
            throw new InvalidRequestException({ message: 'Password is required' });
        }

        if (!payload.username || payload.username?.length == 0) {
            throw new InvalidRequestException({ message: 'Username is required' });
        }

        if (!payload.name || payload.name?.length == 0) {
            throw new InvalidRequestException({ message: 'Name is required' });
        }

        if (payload.password.length < 8) {
            throw new InvalidRequestException({message: 'Passwords length have to be at least 8 characters'})
        }

        const usernameAlreadyExists = await this.repository.findByUsername(payload.username);

        if (!!usernameAlreadyExists) {
            throw new InvalidRequestException({message: 'Username already in use'})
        }

        const createdOperator = await this.repository.create({
            username: payload.username,
            name: payload.name,
            password: await this.cryptService.encrypt(payload.password)
        })

        return {
            id: createdOperator.id,
            username: createdOperator.username,
            name: createdOperator.name,
            created_at: createdOperator.createdAt
        }

    }

}