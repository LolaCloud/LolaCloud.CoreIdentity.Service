import { Entity, PrimaryGeneratedColumn, Column, CreateDateColumn, UpdateDateColumn, ManyToOne, Index } from "typeorm";
import { Operator } from "./operator.entity";

type Relation<T> = T

@Entity()
export class Session {
  @PrimaryGeneratedColumn('uuid')
  id: string;

  @Column()
  @Index()
  tokenIdentifier: string;

  @Column({ nullable: true })
  ipAddress: string;

  @Column({ nullable: true })
  userAgent: string;

  @Column({ nullable: true })
  deviceType: string;

  @Column({ nullable: true })
  location: string;

  @Column({ default: true })
  isActive: boolean;

  @Column({ type: 'timestamp' })
  expiresAt: Date;

  @Column({ type: 'timestamp', nullable: true })
  lastActivityAt: Date;

  @CreateDateColumn()
  createdAt: Date;

  @UpdateDateColumn()
  updatedAt: Date;

  @ManyToOne('Operator', 'sessions', {
    onDelete: 'CASCADE'
  })
  operator: Relation<Operator>; 

}