import { Injectable, OnModuleInit } from "@nestjs/common";
import { OperatorRepository } from "src/application/gateways/operator-repository.gateway";
import { PermissionRepository } from "src/application/gateways/permission-repository.gateway";

@Injectable()
export class SeederService implements OnModuleInit {

  constructor(
    private readonly operatorRepository: OperatorRepository,
    private readonly permissionRepository: PermissionRepository
  ) { }

  async onModuleInit() {
    this.permissionRepository.seeder();
    this.operatorRepository.seeder();
  }

}