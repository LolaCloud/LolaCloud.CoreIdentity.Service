import { Nullable } from "src/domain/utils";
import { Session } from "src/infrastructure/database/typeORM/entities/session.entity";
import { DeepPartial } from "typeorm";

export abstract class SessionRepository {
    abstract create(session: DeepPartial<Session>): Promise<Session>;
    abstract findByTokenIdentifier(tokenIdentifier: string): Promise<Nullable<Session>>
    abstract updateLastActivityAt(sessionId: string): Promise<void>
    abstract HARD_deleteByOperatorId(operatorId: string): Promise<void>
    abstract disableSessionsByOperatorId(operatorId: string): Promise<void>
    abstract findByOperatorId(operatorId: string): Promise<Session[]>
    abstract disableSessionById(sessionId: string): Promise<void>
    abstract findById(sessionId: string): Promise<Nullable<Session>>
}