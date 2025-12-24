import { ForbiddenException, Injectable, NotFoundException } from "@nestjs/common";
import { SessionRepository } from "src/application/gateways/session-repository.gateway";

@Injectable()
export class DisableSessionUseCase {

    constructor(
        private readonly sessionRepository: SessionRepository
    ) {
    }

    async execute(currentOperatorId: string, sessionId: string) {

        const session = await this.sessionRepository.findById(sessionId)

        if (!session) throw new NotFoundException();

        if (session.operator.id != currentOperatorId) throw new ForbiddenException()

            await this.sessionRepository.disableSessionById(sessionId)

        return {
           message: 'Session disabled'
        }
    }

}