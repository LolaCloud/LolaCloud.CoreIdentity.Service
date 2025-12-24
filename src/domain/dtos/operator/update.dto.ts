import { LolaPermissions } from "src/domain/enums/permissions.enum";
import { Nullable } from "src/domain/utils";

export type OperatorUpdateDTO = {
    name: string;
    email: Nullable<string>;
    permissions: LolaPermissions[]
}