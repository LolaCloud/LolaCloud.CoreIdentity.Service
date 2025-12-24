import { Injectable, NestMiddleware, UnauthorizedException } from "@nestjs/common";
import { OperatorRepository } from "src/application/gateways/operator-repository.gateway";
import { SessionRepository } from "src/application/gateways/session-repository.gateway";

@Injectable()
export class OperatorMiddleware implements NestMiddleware {

  constructor(
    private readonly operatorRepository: OperatorRepository,
    private readonly sessionRepository: SessionRepository
  ) { }

  async use(req: any, res: any, next: (error?: Error | any) => void) {
    const { operatorId } = req.internal

    const operator = await this.operatorRepository.findById(operatorId)

    if (!operator) {
      throw new UnauthorizedException()
    }

    operator.permissions = await this.operatorRepository.getPermissions(operator.id);

    this.operatorRepository.updateLastTimeActive(operator.id)
    this.sessionRepository.updateLastActivityAt(req.internal.sessionId)

    req.internal.operator = operator
    next()
  }

}