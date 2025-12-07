import { MigrationInterface, QueryRunner } from "typeorm";

export class Migration1765064221174 implements MigrationInterface {
    name = 'Migration1765064221174'

    public async up(queryRunner: QueryRunner): Promise<void> {
        await queryRunner.query(`ALTER TABLE "lci"."operator" ADD "lastTimeActive" TIMESTAMP`);
    }

    public async down(queryRunner: QueryRunner): Promise<void> {
        await queryRunner.query(`ALTER TABLE "lci"."operator" DROP COLUMN "lastTimeActive"`);
    }

}
