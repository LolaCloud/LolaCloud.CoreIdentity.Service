import { Injectable } from "@nestjs/common";
import { OperatorRepository } from "src/application/gateways/operator-repository.gateway";
import { Nullable } from "src/domain/utils";
import { Operator } from "../entities/operator.entity";
import { InjectRepository } from "@nestjs/typeorm";
import { DeepPartial, Repository } from "typeorm";
import { CryptService } from "src/services/crypt.service";

@Injectable()
export class TypeORMOperatorRepository implements OperatorRepository {

    constructor(
        @InjectRepository(Operator)
        private readonly repository: Repository<Operator>,
        private readonly cryptService: CryptService
    ) {

    }

    async save(operator: Operator): Promise<Operator> {
        return await this.repository.save(operator);
    }

    async findByUsername(username: string): Promise<Nullable<Operator>> {
        return await this.repository.findOne({
            where: {
                username
            }
        })
    }

    async create(operator: DeepPartial<Operator>) {
        const toCreate = this.repository.create(operator);

        return await this.save(toCreate);
    }

    async seeder(): Promise<void> {
        const user = await this.findByUsername('root')
        if (!!user) return;

        await this.repository.save({
            name: 'Root',
            username: 'root',
            password: await this.cryptService.encrypt('toor')
        })
    }

    async deleteById(operatorId: string): Promise<void> {
        await this.repository.softDelete({
          id: operatorId  
        })
    }

    async HARD_deleteById(operatorId: string): Promise<void> {
        await this.repository.delete({
            id: operatorId
        })
    }

    async findById(operatorId: string): Promise<Nullable<Operator>> {
        return await this.repository.findOne({
            where: {
                id: operatorId
            }
        })
    }

    async updateLastTimeActive(operatorId: string): Promise<void> {
        await this.repository.update({
            id: operatorId
        }, {
            lastTimeActive: new Date()
        })
    }

}