import { Nullable } from "src/domain/utils";
import { Column, CreateDateColumn, DeleteDateColumn, Entity, PrimaryGeneratedColumn, UpdateDateColumn } from "typeorm";

@Entity()
export class Operator {

    @PrimaryGeneratedColumn('uuid')
    id: string;

    @Column({
        type: 'varchar',
        length: 255
    })
    username: string;

    @Column({
        type: 'varchar',
        length: 255
    })
    password: string;

    @Column({
        type: 'varchar',
        length: 255
    })
    name: string;

    @CreateDateColumn()
    createdAt: Date;

    @UpdateDateColumn()
    updatedAt: Date;

    @DeleteDateColumn()
    deletedAt?: Date;

    @Column({
        type: 'timestamp',
        nullable: true
    })
    lastTimeActive: Date | null;

}