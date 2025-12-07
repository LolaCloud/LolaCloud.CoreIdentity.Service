import { Injectable } from "@nestjs/common";
import { EnvConfig } from "src/configuration";
import * as jwt from 'jsonwebtoken';

@Injectable()
export class TokenService {

   async generate(payload): Promise<string> {
    var token = jwt.sign(payload, EnvConfig.ENCRYPTION_STRING);

    return token
  }

  async validateToken<T>(token: string): Promise<T> {
    const decoded = jwt.verify(token, EnvConfig.ENCRYPTION_STRING);
    return decoded as T
  }

}