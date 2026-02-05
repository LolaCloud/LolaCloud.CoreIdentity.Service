export type AddDNSRecordDTO = {
    name: string;
    managedZoneId: string;
    ttl: number;
    type: string;
    content: string;
    autoIpFix: boolean;
    proxy: boolean;
}