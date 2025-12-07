import { createParamDecorator, ExecutionContext } from "@nestjs/common";

export const OperatorId = createParamDecorator(
    (_data: unknown, ctx: ExecutionContext) => {
        const request = ctx.switchToHttp().getRequest();
        return request.internal.operatorId;
    }
)

export const Operator = createParamDecorator(
    (_data: unknown, ctx: ExecutionContext) => {
        const request = ctx.switchToHttp().getRequest();
        return request.internal.operator;
    }
)