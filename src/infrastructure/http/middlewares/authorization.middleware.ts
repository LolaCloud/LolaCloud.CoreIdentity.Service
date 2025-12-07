import { Injectable, NestMiddleware, UnauthorizedException } from "@nestjs/common";
import { Request } from "express";
import { TokenService } from "src/services/token.service";

@Injectable()
export class AuthorizationMiddleware implements NestMiddleware {

  constructor(
    private readonly tokenService: TokenService
  ) { }

  async use(req: Request, res: any, next: (error?: Error | any) => void) {
    const authorization = req.headers['authorization']?.split(' ')[1]

    if (!!authorization) {
      try {
        const decoded:any = await this.tokenService.validateToken(authorization)
        
        // @ts-expect-error
        req.internal = {
          operatorId: decoded.operatorId
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