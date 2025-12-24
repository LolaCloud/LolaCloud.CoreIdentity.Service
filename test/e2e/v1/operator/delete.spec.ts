import { INestApplication } from "@nestjs/common";
import { Test } from "@nestjs/testing";
import { OperatorRepository } from "src/application/gateways/operator-repository.gateway";
import { Operator } from "src/infrastructure/database/typeORM/entities/operator.entity";
import { HttpModule } from "src/infrastructure/http/http.module";
import supertest from "supertest";
import { TEST_createTemporaryOperator, TEST_generateRandomOperator } from "test/utils";

describe('E2E::V1::Operator::DELETE', () => {
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
    
        it('Should delete an operator with the data that has been sent', async() => {
            const randomOperator = TEST_generateRandomOperator()     
            const createdOperator = await repository.create(randomOperator);
            
            const request = supertest(app.getHttpServer());
            const response = await request.delete('/v1/operator/' + createdOperator.id)
                .set('Authorization', `Bearer ${accessToken}`)
                .send()

            expect(response.statusCode).toBe(200)
            await repository.HARD_deleteById(createdOperator.id)
        })

        it('Should throw an error when operator id that was received doesnt exists', async() => {
            const request = supertest(app.getHttpServer());
            const response = await request.delete('/v1/operator/hello_world')
                .set('Authorization', `Bearer ${accessToken}`)
                .send()

            expect(response.statusCode).toBe(400)
        })

        it('Should throw an error when operator id that was received is from root user', async() => {
            const rootUser = await repository.findByUsername('root');
            if (!rootUser) {
                expect('Root operator not found').toBe(418)
            }

            const request = supertest(app.getHttpServer());
            const response = await request.delete('/v1/operator/' + (rootUser as Operator).id)
                .set('Authorization', `Bearer ${accessToken}`)
                .send()

            expect(response.statusCode).toBe(400)
        })
    
        afterAll(async() => {
            await deleteFunction();
            await app.close();
        })
    
})