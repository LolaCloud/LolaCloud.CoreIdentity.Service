import { Body, Controller, Delete, Get, HttpCode, Ip, Param, Post, Req } from "@nestjs/common";
import { AuthSignInUseCase } from "src/application/usecases/auth/sign-in.usecase";
import { Operator } from "../decorators/operator.decorator";
import { UpdateOperatorPasswordUseCase } from "src/application/usecases/auth/update-password.usecase";
import { GetSessionsUseCase } from "src/application/usecases/auth/get-sessions.usecase";
import { DisableSessionUseCase } from "src/application/usecases/auth/disable-session.usecase";

@Controller('/v1/auth')
export class AuthController {

    constructor(
        private readonly signInUseCase: AuthSignInUseCase,
        private readonly updatePasswordUseCase: UpdateOperatorPasswordUseCase,
        private readonly getSessionsUseCase: GetSessionsUseCase,
        private readonly disableSessionUseCase: DisableSessionUseCase
    ) { }

    @Post('/sign-in')
    @HttpCode(200)
    async signInRoute(@Body() body, @Ip() ip, @Req() request: Request) {
        return await this.signInUseCase.execute(body, ip, request.headers['user-agent'])
    }


    @Post('/update-password')
    async updatePassword(@Operator() operator, @Body() payload) {
        return this.updatePasswordUseCase.execute(operator.id, payload)
    }

    @Get('/sessions')
    async getOperatorSessions(@Operator() operator, @Req() req) {
        return await this.getSessionsUseCase.execute(operator.id, req.internal.sessionId)
    }

    @Delete('/session/:sessionId')
    async deleteSession(@Operator() operator, @Param() params) {
        return await this.disableSessionUseCase.execute(operator.id, params.sessionId)
    }

}