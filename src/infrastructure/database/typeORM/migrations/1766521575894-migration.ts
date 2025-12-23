import { MigrationInterface, QueryRunner } from "typeorm";

export class Migration1766521575894 implements MigrationInterface {
    name = 'Migration1766521575894'

    public async up(queryRunner: QueryRunner): Promise<void> {
        await queryRunner.query(`CREATE TABLE "lci"."session" ("id" uuid NOT NULL DEFAULT uuid_generate_v4(), "tokenIdentifier" character varying NOT NULL, "ipAddress" character varying, "userAgent" character varying, "deviceType" character varying, "location" character varying, "isActive" boolean NOT NULL DEFAULT true, "expiresAt" TIMESTAMP NOT NULL, "lastActivityAt" TIMESTAMP, "createdAt" TIMESTAMP NOT NULL DEFAULT now(), "updatedAt" TIMESTAMP NOT NULL DEFAULT now(), "operatorId" uuid, CONSTRAINT "PK_f55da76ac1c3ac420f444d2ff11" PRIMARY KEY ("id"))`);
        await queryRunner.query(`CREATE INDEX "IDX_64c001284368cfefbf58ed355a" ON "lci"."session" ("tokenIdentifier") `);
        await queryRunner.query(`ALTER TABLE "lci"."session" ADD CONSTRAINT "FK_5b863660f38f58787483c53b618" FOREIGN KEY ("operatorId") REFERENCES "lci"."operator"("id") ON DELETE NO ACTION ON UPDATE NO ACTION`);
    }

    public async down(queryRunner: QueryRunner): Promise<void> {
        await queryRunner.query(`ALTER TABLE "lci"."session" DROP CONSTRAINT "FK_5b863660f38f58787483c53b618"`);
        await queryRunner.query(`DROP INDEX "lci"."IDX_64c001284368cfefbf58ed355a"`);
        await queryRunner.query(`DROP TABLE "lci"."session"`);
    }

}
