import { MigrationInterface, QueryRunner } from "typeorm";

export class Migration1766526412195 implements MigrationInterface {
    name = 'Migration1766526412195'

    public async up(queryRunner: QueryRunner): Promise<void> {
        await queryRunner.query(`ALTER TABLE "lci"."session" DROP CONSTRAINT "FK_5b863660f38f58787483c53b618"`);
        await queryRunner.query(`ALTER TABLE "lci"."session" ADD CONSTRAINT "FK_5b863660f38f58787483c53b618" FOREIGN KEY ("operatorId") REFERENCES "lci"."operator"("id") ON DELETE CASCADE ON UPDATE NO ACTION`);
    }

    public async down(queryRunner: QueryRunner): Promise<void> {
        await queryRunner.query(`ALTER TABLE "lci"."session" DROP CONSTRAINT "FK_5b863660f38f58787483c53b618"`);
        await queryRunner.query(`ALTER TABLE "lci"."session" ADD CONSTRAINT "FK_5b863660f38f58787483c53b618" FOREIGN KEY ("operatorId") REFERENCES "lci"."operator"("id") ON DELETE NO ACTION ON UPDATE NO ACTION`);
    }

}
