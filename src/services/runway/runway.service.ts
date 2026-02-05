import { Injectable } from "@nestjs/common";
import axios, { AxiosInstance } from "axios";
import { EnvConfig } from "src/configuration";
import { Runway_AddZoneResponse, Runway_CreateCloudflareAccountResponse, Runway_GetAllAccountsResponse, Runway_GetAllZonesResponse, Runway_GetDNSRecordsByManagedZoneIdResponse } from "./runway.types";
import { CreateCloudflareAccountDTO } from "src/domain/dtos/runway/create-cloudflare-account.dto";
import { AddZoneDTO } from "src/domain/dtos/runway/add-zone.dto";
import { AddDNSRecordDTO } from "src/domain/dtos/runway/add-dns-record.dto";

@Injectable()
export class RunwayService {

    private readonly baseAPI: AxiosInstance

    constructor() {
        this.baseAPI = axios.create({
            baseURL: 'http://localhost:3001',
            headers: {
                Authorization: 'Bearer ' + EnvConfig.LOLA_MICROSERVICE_SECRET
            }
        })
    }

    async getAllAccounts(): Promise<Runway_GetAllAccountsResponse> {
        const response = await this.baseAPI.get('/v1/account');

        return response.data;
    }

    async verifyToken(token: string): Promise<boolean> {
        try {
            const response = await this.baseAPI.post('/v1/account/verify-token', {
                token
            })
            if (response.data.message === 'ok') {
                return true
            }
            return false
        } catch (error) {
            return false;
        }
    }

    async getAllZonesFromCloudflare(accountId: string) {
        const response = await this.baseAPI.get('/v1/zone/from-cloudflare', {
            params: {
                accountId
            }
        })

        return response.data;
    }

    async createCloudflareAcount(payload: CreateCloudflareAccountDTO): Promise<Runway_CreateCloudflareAccountResponse> {
        const response = await this.baseAPI.post('/v1/account', payload);
        return response.data;
    }

    async addZone(payload: AddZoneDTO): Promise<Runway_AddZoneResponse> {
        const response = await this.baseAPI.post('/v1/zone', payload)

        return response.data;
    }

    async getAllZones(): Promise<Runway_GetAllZonesResponse> {
        const response = await this.baseAPI.get('/v1/zone');

        return response.data;
    }

    async getDNSRecordsByManagedZoneId(managedZoneId: string): Promise<Runway_GetDNSRecordsByManagedZoneIdResponse> {
        const response = await this.baseAPI.get('/v1/dns-record', {
            params: {
                managedZoneId
            }
        })

        return response.data
    }

    async addDNSRecord(payload: AddDNSRecordDTO) {
        await this.baseAPI.post('/v1/dns-record', payload)
    }

    async updateDNSRecord(id: string, payload: AddDNSRecordDTO) {
        await this.baseAPI.patch('/v1/dns-record', payload, {
            params: {
                id
            }
        })
    }

    async deleteDNSRecord(id: string) {
        await this.baseAPI.delete('/v1/dns-record', {
            params: {
                id
            }
        })
    }

    async removeZone(id: string) {
        await this.baseAPI.delete('/v1/zone', {
            params: { id }
        })
    }

}