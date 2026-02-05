import { BadRequestException, Injectable, InternalServerErrorException } from "@nestjs/common";
import { CreateCloudflareAccountDTO } from "src/domain/dtos/runway/create-cloudflare-account.dto";
import { RunwayService } from "src/services/runway/runway.service";
import snakeCaseKeys from 'snakecase-keys'

@Injectable()
export class CreateCloudflareAccountUseCase {

    constructor(
        private readonly runwayService: RunwayService
    ) { }

    async execute(payload: CreateCloudflareAccountDTO) {

        if (!payload.name || payload.name.length < 3) {
            throw new BadRequestException('Name is required and at least 3 chars')
        }

        if (!payload.token) {
            throw new BadRequestException('Token is required')
        }

        try {
            const response = await this.runwayService.createCloudflareAcount(payload)
            const ac = response.account

            return {
                account: snakeCaseKeys(ac, {deep: true})
            }
        } catch (error) {
            throw new BadRequestException(error.response.data.message)
        }
    }

}
