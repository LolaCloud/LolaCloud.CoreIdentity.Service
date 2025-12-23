import { Nullable } from "src/domain/utils";
import { Session } from "src/infrastructure/database/typeORM/entities/session.entity";
import { DeepPartial } from "typeorm";

export abstract class SessionRepository {
    abstract create(session: DeepPartial<Session>): Promise<Session>;
    abstract findByTokenIdentifier(tokenIdentifier: string): Promise<Nullable<Session>>
    abstract lastActivityAt(sessionId: string): Promise<void>
}