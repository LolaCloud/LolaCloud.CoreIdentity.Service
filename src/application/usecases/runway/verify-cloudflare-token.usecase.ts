import { Injectable } from "@nestjs/common";
import { RunwayService } from "src/services/runway/runway.service";

@Injectable()
export class VerifyCloudflareTokenUseCase {

    constructor(
        private readonly runwayService: RunwayService
    ){}

    async execute(token: string) {
        const success = await this.runwayService.verifyToken(token);

        return {
            success
        }
    }

}