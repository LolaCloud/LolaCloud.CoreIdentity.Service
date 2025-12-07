import { INestApplication } from "@nestjs/common"
import { Test } from "@nestjs/testing";
import { OperatorRepository } from "src/application/gateways/operator-repository.gateway";
import { HttpModule } from "src/infrastructure/http/http.module";
import { fakerPT_BR as faker } from "@faker-js/faker";
import supertest from "supertest";
import { CryptService } from "src/services/crypt.service";

describe('E2E::V1::Auth::SignIn', () => {
    let app: INestApplication;
    let operatorRepository: OperatorRepository;
    let cryptService: CryptService

    let temporaryOperator;

    beforeAll(async () => {
        const module = await Test.createTestingModule({
            imports: [
                HttpModule
            ]
        }).compile();

        app = module.createNestApplication();
        operatorRepository = module.get(OperatorRepository);
        cryptService = module.get(CryptService)

        let password = faker.internet.password()
        let temp = await operatorRepository.create({
            createdAt: new Date(),
            name: faker.person.fullName(),
            password: await cryptService.encrypt(password),
            updatedAt: new Date(),
            username: faker.internet.username()
        })

        temporaryOperator = {
            ...temp,
            password
        }

        await app.init();
    })

    it('Should return a token when receive valid credentials', async() => {
        const request = supertest(app.getHttpServer());
        const response = await request.post('/v1/auth/sign-in').send({
            username: temporaryOperator.username,
            password: temporaryOperator.password
        }) 

        expect(response.statusCode).toBe(200)
        expect(response.body.access_token).toBeTruthy();
        expect(response.body.access_token?.length).toBeGreaterThan(10);
    })

    it('Should return invalid credentials when received invalid credentials', async() => {
        const request = supertest(app.getHttpServer());
        const response = await request.post('/v1/auth/sign-in').send({
            username: faker.internet.username(),
            password: faker.internet.password()
        });

        expect(response.statusCode).toBe(401)
    })

    it('Should return invalid credentials when no password is received', async() => {
        const request = supertest(app.getHttpServer());
        const response = await request.post('/v1/auth/sign-in').send({
            username: faker.internet.username()
        });
        expect(response.statusCode).toBe(401)
    })

    it('Should return invalid credentials when no username is received', async() => {
        const request = supertest(app.getHttpServer());
        const response = await request.post('/v1/auth/sign-in').send({
            password: faker.internet.password()
        });
        expect(response.statusCode).toBe(401)
    })

    it('Should return invalid credentials when payload is too long', async() => {
        const request = supertest(app.getHttpServer());
        const response = await request.post('/v1/auth/sign-in').send({
            username: faker.string.alpha({length: {min: 300, max: 400}}),
            password: faker.string.alpha({length: {min: 300, max: 400}}),
        });
        expect(response.statusCode).toBe(401)
    })

    afterAll(async () => {
        await operatorRepository.HARD_deleteById(temporaryOperator.id)
        await app.close()
    })
})