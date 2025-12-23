import { Injectable } from "@nestjs/common";
import { InjectRepository } from "@nestjs/typeorm";
import { Repository } from "typeorm";
import { PermissionRepository } from "src/application/gateways/permission-repository.gateway";
import { Permission } from "../entities/permission.entity";
import { LolaPermissions } from "src/domain/enums/permissions.enum";

@Injectable()
export class TypeORMPermissionRepository implements PermissionRepository {

    constructor(
        @InjectRepository(Permission)
        private readonly repository: Repository<Permission>,
    ) {

    }

    async seeder(): Promise<void> {
        const permissions: LolaPermissions[] = [
            LolaPermissions.LOLA_ALL,
            LolaPermissions.LCI_ALL,
            LolaPermissions.LCI_OPERATOR_CREATE,
            LolaPermissions.LCI_OPERATOR_DELETE,
            LolaPermissions.LCI_OPERATOR_UPDATE,
            LolaPermissions.LCI_OPERATOR_ALL
        ];

        for (const permission of permissions) {
            const alreadyExists = await this.repository.findBy({
                slug: permission
            })
            if (alreadyExists.length === 0) {
                await this.repository.save({
                    slug: permission,
                    service: permission.split('::')[0]
                })
            }
        }
    }

    async findByPermission(permission: LolaPermissions): Promise<Permission> {
        const p = await this.repository.findOne({
            where: {
                slug: permission
            }
        })

        return p as Permission
    }

}