import { Body, Controller, Delete, Get, Patch, Post, Query } from "@nestjs/common";
import { AddDNSRecordUseCase } from "src/application/usecases/runway/add-dns-record.usecase";
import { AddZoneUseCase } from "src/application/usecases/runway/add-zone.usecase";
import { CreateCloudflareAccountUseCase } from "src/application/usecases/runway/create-cloudflare-account.usecase";
import { DeleteDNSRecordUseCase } from "src/application/usecases/runway/delete-dns-record.usecase";
import { GetAllCloudflareAccountsUseCase } from "src/application/usecases/runway/get-all-accounts.usecase";
import { GetZonesUseCase } from "src/application/usecases/runway/get-all-zones.usecase";
import { GetDNSRecordsByManagedZoneIdUseCase } from "src/application/usecases/runway/get-dns-records-by-managed-zone-id.usecase";
import { GetZonesFromCloudflareUseCase } from "src/application/usecases/runway/get-zones-from-cloudflare.usecase";
import { RemoveZoneUseCase } from "src/application/usecases/runway/remove-zone-record.usecase";
import { UpdateDNSRecordUseCase } from "src/application/usecases/runway/update-dns-record.usecase";
import { VerifyCloudflareTokenUseCase } from "src/application/usecases/runway/verify-cloudflare-token.usecase";

@Controller('/v1/runway')
export class RunwayController {

    constructor(
        private readonly getAllCloudflareAccounts: GetAllCloudflareAccountsUseCase,
        private readonly verifyCloudflareToken: VerifyCloudflareTokenUseCase,
        private readonly getZonesFromCloudflare: GetZonesFromCloudflareUseCase,
        private readonly createCloudflareAccount: CreateCloudflareAccountUseCase,
        private readonly addZoneUseCase: AddZoneUseCase,
        private readonly getAllZonesUseCase: GetZonesUseCase,
        private readonly getDNSRecordsByManagedZoneIdUseCase: GetDNSRecordsByManagedZoneIdUseCase,
        private readonly addDNSRecordUseCase: AddDNSRecordUseCase,
        private readonly updateDNSRecordUseCase: UpdateDNSRecordUseCase,
        private readonly deleteDNSRecordUseCase: DeleteDNSRecordUseCase,
        private readonly removeZoneUseCase: RemoveZoneUseCase
    ) { }

    @Get('account')
    async getAllAccounts() {
        return await this.getAllCloudflareAccounts.execute()
    }

    @Post('verify-token')
    async verifyToken(@Body() body) {
        return await this.verifyCloudflareToken.execute(body.token)
    }

    @Post('account')
    async createAccount(@Body() body) {
        return await this.createCloudflareAccount.execute(body)
    }

    @Get('cloudflare-zones')
    async getCloudflareZones(@Query() query) {
        const accountId = query.accountId;
        return await this.getZonesFromCloudflare.execute(accountId);
    }

    @Post('add-zone')
    async addZone(@Body() body) {
        return await this.addZoneUseCase.execute(body)
    }

    @Get('zone')
    async getAllZones() {
        return await this.getAllZonesUseCase.execute()
    }

    @Get('dns-record')
    async getDNSRecordsByManagedZoneId(@Query() query) {
        return await this.getDNSRecordsByManagedZoneIdUseCase.execute(query.managedZoneId)
    }

    @Post('dns-record')
    async addDNSRecord(@Body() body) {
        return await this.addDNSRecordUseCase.execute(body);
    }

    @Patch('dns-record')
    async updateDNSRecord(@Query() query,  @Body() body) {
        return await this.updateDNSRecordUseCase.execute(query.id, body)
    }

    @Delete('dns-record')
    async deleteDNSRecord(@Query() query) {
        return await this.deleteDNSRecordUseCase.execute(query.id);
    }

    @Delete('zone')
    async removeZone(@Query() query) {
        return await this.removeZoneUseCase.execute(query.id)
    }

}