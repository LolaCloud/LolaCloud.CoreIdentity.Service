import { INestApplication } from "@nestjs/common"
import { Test } from "@nestjs/testing";
import { OperatorRepository } from "src/application/gateways/operator-repository.gateway";
import { HttpModule } from "src/infrastructure/http/http.module";
import supertest from "supertest";
import { TEST_createTemporaryOperator, TEST_generateRandomOperator } from "test/utils";

describe('E2E::V1::Operator::Post', () => {
    let app: INestApplication;
    let accessToken;
    let operator;
    let deleteFunction;
    let repository: OperatorRepository;

    beforeAll(async() => {

        const moduleRef = await Test.createTestingModule({
            imports: [
                HttpModule
            ]
        }).compile();

        app = moduleRef.createNestApplication()

        repository = moduleRef.get(OperatorRepository)

        const response = await TEST_createTemporaryOperator(moduleRef);

        accessToken = response.accessToken;
        operator = response.createdOperator;
        deleteFunction = response.deleteFunction

        await app.init();
    })

    it('Should create an operator with the data that has been sent - WITHOUT EMAIL', async() => {
        const randomOperator = TEST_generateRandomOperator()    
        randomOperator.email = undefined    
        
        const request = supertest(app.getHttpServer());
        const response = await request.post('/v1/operator')
            .set('Authorization', `Bearer ${accessToken}`)
            .send({
                username: randomOperator.username,
                name: randomOperator.name,
                password: randomOperator.password
            })

        expect(response.statusCode).toEqual(201);
        expect(response.body.id).toBeTruthy();
        expect(response.body.username).toBe(randomOperator.username);
        expect(response.body.name).toBe(randomOperator.name);
        expect(response.body.email).toBe(null)

        await repository.HARD_deleteById(response.body.id);
    })

    it('Should create an operator with the data that has been sent - WITH EMAIL', async() => {
        const randomOperator = TEST_generateRandomOperator()        
        
        const request = supertest(app.getHttpServer());
        const response = await request.post('/v1/operator')
            .set('Authorization', `Bearer ${accessToken}`)
            .send({
                username: randomOperator.username,
                name: randomOperator.name,
                password: randomOperator.password,
                email: randomOperator.email
            })

        expect(response.statusCode).toEqual(201);
        expect(response.body.id).toBeTruthy();
        expect(response.body.username).toBe(randomOperator.username);
        expect(response.body.name).toBe(randomOperator.name);
        expect(response.body.email).toBe(randomOperator.email)

        await repository.HARD_deleteById(response.body.id);
    })

    it('Should return a 401 status code when no password is sent', async() => {
        const randomOperator = TEST_generateRandomOperator();
        const request = supertest(app.getHttpServer());
        const response = await request.post('/v1/operator')
            .set('Authorization', `Bearer ${accessToken}`)
            .send({
                username: randomOperator.username,
                name: randomOperator.name,
            })

        expect(response.statusCode).toBe(400);
    })

    it('Should return a 401 status code when no username is sent', async() => {
        const randomOperator = TEST_generateRandomOperator();
        const request = supertest(app.getHttpServer());
        const response = await request.post('/v1/operator')
            .set('Authorization', `Bearer ${accessToken}`)
            .send({
                name: randomOperator.name,
                password: randomOperator.password
            })

        expect(response.statusCode).toBe(400);
    })

    it('Should return a 401 status code when no name is sent', async() => {
        const randomOperator = TEST_generateRandomOperator();
        const request = supertest(app.getHttpServer());
        const response = await request.post('/v1/operator')
            .set('Authorization', `Bearer ${accessToken}`)
            .send({
                password: randomOperator.password,
                username: randomOperator.username
            })

        expect(response.statusCode).toBe(400);
    })

    afterAll(async() => {
        await deleteFunction();
        await app.close();
    })


})