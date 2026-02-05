import { BadRequestException, Injectable } from "@nestjs/common";
import { RunwayService } from "src/services/runway/runway.service";

@Injectable()
export class RemoveZoneUseCase {

    constructor(
        private readonly runway: RunwayService
    ){}

    async execute(id: string) {
        try {
            await this.runway.removeZone(id);
            return {
                success: true
            }
        } catch (error) {
            throw new BadRequestException(error.response.data.message);
        }
    }

}