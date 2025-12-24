import { Injectable, CanActivate, ExecutionContext, ForbiddenException } from '@nestjs/common';
import { Reflector } from '@nestjs/core';
import { LolaPermissions } from 'src/domain/enums/permissions.enum';
import { PERMISSION_KEY } from '../decorators/permission.decorator';
import { Operator } from 'src/infrastructure/database/typeORM/entities/operator.entity';

@Injectable()
export class PermissionsGuard implements CanActivate {
  constructor(private reflector: Reflector) {}

  async canActivate(context: ExecutionContext): Promise<boolean> {
    const requiredPermission = this.reflector.getAllAndOverride<LolaPermissions>(PERMISSION_KEY, [
      context.getHandler(),
      context.getClass(),
    ]);

    if (!requiredPermission) return true

    const operator = context.switchToHttp().getRequest().internal.operator as Operator;

    const isRoot = operator.username === 'root';
    if (isRoot) return true;

    if (operator.permissions.some(permission => permission.slug === LolaPermissions.LOLA_ALL)) return true;

    const hasExactPermission = operator.permissions.some(
      (permission) => permission.slug === requiredPermission
    );
    if (hasExactPermission) return true;

    const hasDynamicPermission = operator.permissions.some((permission) => {
      if (!permission.slug.includes('*')) return false;

      const regexPattern = permission.slug
        .replace(/[.+^${}()|[\]\\]/g, '\\$&') 
        .replace(/\*/g, '.*');

      const regex = new RegExp(`^${regexPattern}$`);
      return regex.test(requiredPermission);
    });

    if (hasDynamicPermission) return true;


    throw new ForbiddenException();
  }
}