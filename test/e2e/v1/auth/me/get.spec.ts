import { INestApplication } from "@nestjs/common";
import { Test } from "@nestjs/testing";
import { HttpModule } from "src/infrastructure/http/http.module";
import supertest from "supertest";
import { TEST_createTemporaryOperator } from "test/utils";

describe("E2E::V1::Auth::Me", () => {
    let app: INestApplication;
    let accessToken
    let operator
    let deleteFunction

    beforeAll(async () => {
        const moduleRef = await Test.createTestingModule({
            imports: [
                HttpModule
            ]
        }).compile();

        app = moduleRef.createNestApplication()
        await app.init();

        const response = await TEST_createTemporaryOperator(moduleRef)
        accessToken = response.accessToken;
        operator = response.createdOperator;
        deleteFunction = response.deleteFunction
    })

    it('Should return user data by access_token received in headers', async() => {
        const request = supertest(app.getHttpServer())
        const response = await request.get('/v1/auth/me').set('Authorization', `Bearer ${accessToken}`)
        expect(response.statusCode).toBe(200);
    })

    it('Should return 401 when endpoint doesnt receive a authorization header', async() => {
        const request = supertest(app.getHttpServer())
        const response = await request.get('/v1/auth/me');

        expect(response.statusCode).toBe(401);
    })

    afterAll(async() => {
        await deleteFunction();
        await app.close();
    })
})