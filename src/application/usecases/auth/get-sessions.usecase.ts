import { Injectable } from "@nestjs/common";
import { SessionRepository } from "src/application/gateways/session-repository.gateway";

@Injectable()
export class GetSessionsUseCase {

    constructor(
        private readonly sessionRepository: SessionRepository
    ) {
    }

    async execute(operatorId: string, currentSessionId: string) {

        const sessions = await this.sessionRepository.findByOperatorId(operatorId)

        return {
            sessions: sessions.map(session => ({
                id: session.id,
                token_identifier: session.tokenIdentifier,
                current: currentSessionId === session.tokenIdentifier,
                ip_address: session.ipAddress,
                user_agent: session.userAgent,
                device_type: session.deviceType,
                location: session.location,
                is_active: session.isActive,
                expires_at: session.expiresAt,
                last_activity_at: session.lastActivityAt,
                created_at: session.createdAt,
                updated_at: session.updatedAt
            }))
        }
    }

}