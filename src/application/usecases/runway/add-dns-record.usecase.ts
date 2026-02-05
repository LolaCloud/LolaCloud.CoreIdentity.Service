import { BadRequestException, Injectable } from "@nestjs/common";
import { AddDNSRecordDTO } from "src/domain/dtos/runway/add-dns-record.dto";
import { RunwayService } from "src/services/runway/runway.service";

@Injectable()
export class AddDNSRecordUseCase {

    constructor(
        private readonly runway: RunwayService
    ){}

    async execute(payload: AddDNSRecordDTO) {
        try {
            await this.runway.addDNSRecord(payload);
            return {
                success: true
            }
        } catch (error) {
            throw new BadRequestException(error.response.data.message);
        }
    }

}