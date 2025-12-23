import { Injectable, NestMiddleware, UnauthorizedException } from "@nestjs/common";
import { Request } from "express";
import { SessionRepository } from "src/application/gateways/session-repository.gateway";
import { TokenService } from "src/services/token.service";

@Injectable()
export class AuthorizationMiddleware implements NestMiddleware {

  constructor(
    private readonly tokenService: TokenService,
    private readonly sessionRepository: SessionRepository
  ) { }

  async use(req: Request, res: any, next: (error?: Error | any) => void) {
    const authorization = req.headers['authorization']?.split(' ')[1]

    if (!!authorization) {
      try {
        const decoded:any = await this.tokenService.validateToken(authorization)

        const session = await this.sessionRepository.findByTokenIdentifier(decoded.sid);
        if (!session || session.expiresAt < new Date() || !session.isActive) {
          throw new UnauthorizedException()
        }

        this.sessionRepository.lastActivityAt(session.id)
        
        // @ts-expect-error
        req.internal = {
          operatorId: decoded.sub,
          sessionId: decoded.sid
        }
        next()
      } catch (error) {
        throw new UnauthorizedException(error.message)
      }
    } else {
      throw new UnauthorizedException()
    }
  }

}