import { Operator } from "src/infrastructure/database/typeORM/entities/operator.entity";
import { fakerPT_BR as faker } from "@faker-js/faker";
import { DeepPartial } from "typeorm";
import { TestingModule } from "@nestjs/testing";
import { OperatorRepository } from "src/application/gateways/operator-repository.gateway";
import { AuthSignInUseCase } from "src/application/usecases/auth/sign-in.usecase";
import { CryptService } from "src/services/crypt.service";

export function TEST_generateRandomOperator(): DeepPartial<Operator>  {
    return {
        createdAt: new Date(),
        name: faker.person.firstName(),
        password: faker.internet.password(),
        username: faker.internet.username()
    }
}

export async function TEST_createTemporaryOperator(moduleRef: TestingModule): Promise<{original: Operator, createdOperator: Operator, accessToken: string, deleteFunction: () => Promise<void>}> {
    const partialOperator = TEST_generateRandomOperator();

    let repository = moduleRef.get(OperatorRepository);
    let signInUseCase = moduleRef.get(AuthSignInUseCase);
    let cryptService = moduleRef.get(CryptService)

    const operator = await repository.create({
        ...partialOperator,
        password: await cryptService.encrypt(partialOperator.password as string)
    })
    partialOperator.id = operator.id

    const accessToken = ((await signInUseCase.execute({
        password: partialOperator.password as string, 
        username: partialOperator.username as string
    })).access_token)

    return {
        createdOperator: operator,
        accessToken,
        original: partialOperator as Operator,
        deleteFunction: async() => repository.HARD_deleteById(operator.id)
    }
}