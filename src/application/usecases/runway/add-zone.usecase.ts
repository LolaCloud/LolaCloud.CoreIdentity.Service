import { BadRequestException, Injectable, InternalServerErrorException } from "@nestjs/common";
import { AddZoneDTO } from "src/domain/dtos/runway/add-zone.dto";
import snakeCaseKeys from 'snakecase-keys'
import { RunwayService } from "src/services/runway/runway.service";

@Injectable()
export class AddZoneUseCase {

    constructor(
        private readonly runwayService: RunwayService
    ) { }

    async execute(payload: AddZoneDTO) {

        if (!payload.accountId || payload.accountId.length < 3) {
            throw new BadRequestException('AccountId is required')
        }

        if (!payload.zoneId) {
            throw new BadRequestException('ZoneId is required')
        }

        try {
            const response = await this.runwayService.addZone(payload)

            return {
                zone: snakeCaseKeys(response, {deep: true})
            }
        } catch (error) {
            throw new BadRequestException(error.response.data.message)
        }
    }

}
