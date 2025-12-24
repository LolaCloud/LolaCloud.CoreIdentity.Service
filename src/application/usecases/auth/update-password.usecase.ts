import { Injectable } from "@nestjs/common";
import { OperatorRepository } from "src/application/gateways/operator-repository.gateway";
import { UpdatePasswordDTO } from "src/domain/dtos/auth/update-password.dto";
import { InvalidCredentialsException } from "src/infrastructure/http/exceptions/auth/invalid-credentials.exception";
import { InvalidRequestException } from "src/infrastructure/http/exceptions/auth/invalid-request.exception";
import { CryptService } from "src/services/crypt.service";

@Injectable()
export class UpdateOperatorPasswordUseCase {

    constructor(
        private readonly operatorRepository: OperatorRepository,
        private readonly cryptService: CryptService,
    ) {}

    async execute(operatorId: string, payload: UpdatePasswordDTO) {

        if (!payload.newPassword || payload.newPassword?.length == 0) {
            throw new InvalidRequestException({message: 'Password is required'});
        }

        const operator = await this.operatorRepository.findById(operatorId);
        if (!operator) {
            throw new InvalidCredentialsException()
        }

        operator.password = await this.cryptService.encrypt(payload.newPassword)
        this.operatorRepository.save(operator)

        return {
            message: 'Password changed'
        }
    }

}