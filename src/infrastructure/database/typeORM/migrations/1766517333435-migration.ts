import { MigrationInterface, QueryRunner } from "typeorm";

export class Migration1766517333435 implements MigrationInterface {
    name = 'Migration1766517333435'

    public async up(queryRunner: QueryRunner): Promise<void> {
        await queryRunner.query(`ALTER TABLE "lci"."operator" ADD "email" character varying(255)`);
    }

    public async down(queryRunner: QueryRunner): Promise<void> {
        await queryRunner.query(`ALTER TABLE "lci"."operator" DROP COLUMN "email"`);
    }

}
