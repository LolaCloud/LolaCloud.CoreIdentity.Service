import { Injectable } from "@nestjs/common";
import { Nullable } from "src/domain/utils";
import { InjectRepository } from "@nestjs/typeorm";
import { Repository } from "typeorm";
import { Session } from "../entities/session.entity";
import { SessionRepository } from "src/application/gateways/session-repository.gateway";

@Injectable()
export class TypeORMSessionRepository implements SessionRepository {

    constructor(
        @InjectRepository(Session)
        private readonly repository: Repository<Session>,
    ) {

    }

    async create(session: Session): Promise<Session> {
        return await this.repository.save(session);
    }

    async findByTokenIdentifier(tokenIdentifier: string): Promise<Nullable<Session>> {
        return await this.repository.findOneBy({
            tokenIdentifier
        })
    }

    async updateLastActivityAt(sessionId: string): Promise<void> {
        await this.repository.update(sessionId, {
            lastActivityAt: new Date()
        })
    }

    async HARD_deleteByOperatorId(operatorId: string): Promise<void> {
        await this.repository.delete({
            operator: {
                id: operatorId
            }
        })
    }

    async disableSessionsByOperatorId(operatorId: string): Promise<void> {
        await this.repository.update({
            operator: {
                id: operatorId
            }
        }, {
            isActive: false
        })
    }

    async findByOperatorId(operatorId: string): Promise<Session[]> {
        return await this.repository.find({
            where: {
                operator: {
                    id: operatorId
                },
                isActive: true
            }
        })
    }

    async disableSessionById(sessionId: string): Promise<void> {
        await this.repository.update({
            id: sessionId
        }, {
            isActive: false,
        })
    }

    async findById(sessionId: string): Promise<Nullable<Session>> {
        return await this.repository.findOne({
            where: {
                id: sessionId
            },
            relations: {
                operator: true
            }
        })
    }
 
}