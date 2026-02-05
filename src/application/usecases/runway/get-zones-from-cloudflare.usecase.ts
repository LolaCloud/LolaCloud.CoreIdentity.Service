import { Injectable } from "@nestjs/common";
import { RunwayService } from "src/services/runway/runway.service";

@Injectable()
export class GetZonesFromCloudflareUseCase {
    
    constructor(
        private readonly runwayService: RunwayService
    ){}

    async execute(accountId: string) {
        const response = await this.runwayService.getAllZonesFromCloudflare(accountId);

        return {
            zones: response
        }
    }

}