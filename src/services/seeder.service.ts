import { Injectable, OnModuleInit } from "@nestjs/common";
import { OperatorRepository } from "src/application/gateways/operator-repository.gateway";

@Injectable()
export class SeederService implements OnModuleInit {

  constructor(
    private readonly operatorRepository: OperatorRepository,
  ) { }

  async onModuleInit() {
    this.operatorRepository.seeder()
  }

}