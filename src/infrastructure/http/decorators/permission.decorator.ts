import { SetMetadata } from '@nestjs/common';
import { LolaPermissions } from 'src/domain/enums/permissions.enum';

export const PERMISSION_KEY = 'lola_permission';

export const LolaPermission = (permission: LolaPermissions) => 
  SetMetadata(PERMISSION_KEY, permission);