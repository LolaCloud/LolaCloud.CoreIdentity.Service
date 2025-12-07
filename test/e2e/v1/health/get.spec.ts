import { INestApplication } from "@nestjs/common"
import { Test } from '@nestjs/testing'
import { HealthController } from "src/infrastructure/http/controllers/health.controller";
import supertest from 'supertest'

describe("E2E::V1::Health", () => {
    let app: INestApplication;

    beforeAll(async() => {
        const module = await Test.createTestingModule({
            controllers: [
                HealthController
            ]
        }).compile();

        app = module.createNestApplication();
        await app.init();
    })

    it('Should return 200 http status code', async() => {
        const request = supertest(app.getHttpServer())
        const response = await request.get('/v1/health');

        expect(response.statusCode).toBe(200)
    })

    afterAll(async() => {
        await app.close();
    })
})