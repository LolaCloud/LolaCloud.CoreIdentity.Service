import { Injectable } from "@nestjs/common";
import { EnvConfig } from "src/configuration";
import * as bcrypt from 'bcrypt'

@Injectable()
export class CryptService {

    async compare(a: string, encrypted: string): Promise<boolean> {
    return await bcrypt.compare(a, encrypted);
  }

  async encrypt(a: string): Promise<string> {
    return await bcrypt.hash(a, EnvConfig.SALT_ROUNDS)
  }

}