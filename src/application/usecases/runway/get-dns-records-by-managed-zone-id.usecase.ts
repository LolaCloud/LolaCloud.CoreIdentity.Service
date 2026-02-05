import { BadRequestException, Injectable } from "@nestjs/common";
import { RunwayService } from "src/services/runway/runway.service";
import snakeCaseKeys from 'snakecase-keys'

@Injectable()
export class GetDNSRecordsByManagedZoneIdUseCase {

    constructor(
        private readonly runwayService: RunwayService
    ) { }

    async execute(managedZoneId: string) {
        try {
            const records = await this.runwayService.getDNSRecordsByManagedZoneId(managedZoneId);

            return {
                records: records.records.map(record => snakeCaseKeys(record, { deep: true }))
            }
        } catch (error) {
            throw new BadRequestException(error.response.data.message)
        }
    }

}
