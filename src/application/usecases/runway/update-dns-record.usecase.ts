import { BadRequestException, Injectable } from "@nestjs/common";
import { AddDNSRecordDTO } from "src/domain/dtos/runway/add-dns-record.dto";
import { RunwayService } from "src/services/runway/runway.service";

@Injectable()
export class UpdateDNSRecordUseCase {

    constructor(
        private readonly runway: RunwayService
    ){}

    async execute(id: string, payload: AddDNSRecordDTO) {
        try {
            await this.runway.updateDNSRecord(id, payload);
            return {
                success: true
            }
        } catch (error) {
            console.log(error.response)
            throw new BadRequestException(error.response.data.message);
        }
    }

}