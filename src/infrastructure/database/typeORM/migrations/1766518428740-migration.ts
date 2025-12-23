import { MigrationInterface, QueryRunner } from "typeorm";

export class Migration1766518428740 implements MigrationInterface {
    name = 'Migration1766518428740'

    public async up(queryRunner: QueryRunner): Promise<void> {
        await queryRunner.query(`CREATE TABLE "lci"."permission" ("slug" character varying NOT NULL, "service" character varying NOT NULL, CONSTRAINT "PK_3379e3b123dac5ec10734b8cc86" PRIMARY KEY ("slug"))`);
        await queryRunner.query(`CREATE TABLE "lci"."operator_permissions" ("operator_id" uuid NOT NULL, "permission_slug" character varying NOT NULL, CONSTRAINT "PK_2206110a017dfdd15707f11e173" PRIMARY KEY ("operator_id", "permission_slug"))`);
        await queryRunner.query(`CREATE INDEX "IDX_06e31246129f09a6683cdc4d5c" ON "lci"."operator_permissions" ("operator_id") `);
        await queryRunner.query(`CREATE INDEX "IDX_16c9770963bee67514396bd7b0" ON "lci"."operator_permissions" ("permission_slug") `);
        await queryRunner.query(`ALTER TABLE "lci"."operator_permissions" ADD CONSTRAINT "FK_06e31246129f09a6683cdc4d5c8" FOREIGN KEY ("operator_id") REFERENCES "lci"."operator"("id") ON DELETE CASCADE ON UPDATE CASCADE`);
        await queryRunner.query(`ALTER TABLE "lci"."operator_permissions" ADD CONSTRAINT "FK_16c9770963bee67514396bd7b0e" FOREIGN KEY ("permission_slug") REFERENCES "lci"."permission"("slug") ON DELETE NO ACTION ON UPDATE NO ACTION`);
    }

    public async down(queryRunner: QueryRunner): Promise<void> {
        await queryRunner.query(`ALTER TABLE "lci"."operator_permissions" DROP CONSTRAINT "FK_16c9770963bee67514396bd7b0e"`);
        await queryRunner.query(`ALTER TABLE "lci"."operator_permissions" DROP CONSTRAINT "FK_06e31246129f09a6683cdc4d5c8"`);
        await queryRunner.query(`DROP INDEX "lci"."IDX_16c9770963bee67514396bd7b0"`);
        await queryRunner.query(`DROP INDEX "lci"."IDX_06e31246129f09a6683cdc4d5c"`);
        await queryRunner.query(`DROP TABLE "lci"."operator_permissions"`);
        await queryRunner.query(`DROP TABLE "lci"."permission"`);
    }

}
