import { Column, Entity, ManyToMany, PrimaryColumn } from "typeorm";
import { Operator } from "./operator.entity";

@Entity()
export class Permission {

    @PrimaryColumn()
    slug: string;

    @Column()
    service: string;

    @ManyToMany(() => Operator, (operator) => operator.permissions)
    operators: Operator[]

}