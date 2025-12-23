import { LolaPermissions } from "src/domain/enums/permissions.enum";
import { Permission } from "src/infrastructure/database/typeORM/entities/permission.entity";

export abstract class PermissionRepository {
    abstract seeder(): Promise<void>
    abstract findByPermission(permission: LolaPermissions): Promise<Permission>
}