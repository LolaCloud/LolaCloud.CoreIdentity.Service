import { Injectable } from "@nestjs/common";
import { RunwayService } from "src/services/runway/runway.service";
import snakeCaseKeys from 'snakecase-keys'

@Injectable()
export class GetZonesUseCase {
    
    constructor(
        private readonly runwayService: RunwayService
    ){}

    async execute() {
        const response = await this.runwayService.getAllZones();

        return {
           zones: snakeCaseKeys(response.zones, {deep: true})
        }
    }

}
