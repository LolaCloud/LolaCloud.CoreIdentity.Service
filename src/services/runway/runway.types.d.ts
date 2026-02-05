import { Nullable } from "src/domain/utils";

type CloudflareAccount = {
    id: string;
    name: string;
    status: string;
    createdAt: Date;
    updatedAt: Date;
}

type Zone = {
    id: string;
    zoneId: string;
    name: string;
    createdAt: Date;
    updatedAt: Date;
}

type DNSRecord = {
    id: string,
    dnsId: string,
    name: string,
    type: string,
    content: string,
    proxied: boolean,
    autoIpFix: boolean,
    lastSyncIp: Nullable<Date>,
    comment: Nullable<string>
}

export type Runway_GetAllAccountsResponse = {
    "accounts": (Account & { zones: Zone[] })[]
}

export type Runway_CreateCloudflareAccountResponse = {
    account: Account;
}

export type Runway_AddZoneResponse = {
    id: string;
    zoneId: string;
    name: string;
    records: DNSRecord[];
    cloudflareAccount: CloudflareAccount;
    createdAt: Date;
    updatedAt: Date;
}

export type Runway_GetAllZonesResponse = {
    zones: Zone[]
}

export type Runway_GetDNSRecordsByManagedZoneIdResponse = {
    records: DNSRecord[]
}