import { Column, CreateDateColumn, DeleteDateColumn, Entity, JoinTable, ManyToMany, PrimaryGeneratedColumn, UpdateDateColumn } from "typeorm";
import { Permission } from "./permission.entity";

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

    @Column({
        type: 'varchar',
        length: 255,
        default: null,
        nullable: true
    })
    email: string | null

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

    @ManyToMany(() => Permission, (permission) => permission.operators)
    @JoinTable({
        name: 'operator_permissions',
        joinColumn: { name: 'operator_id', referencedColumnName: 'id' },
        inverseJoinColumn: { name: 'permission_slug', referencedColumnName: 'slug' }
    })
    permissions: Permission[];
}