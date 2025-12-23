import { Body, Controller, Get, HttpCode, Ip, Post, Req } from "@nestjs/common";
import { AuthSignInUseCase } from "src/application/usecases/auth/sign-in.usecase";
import { Operator } from "../decorators/operator.decorator";
import { AuthMeUseCase } from "src/application/usecases/auth/me.usecase";

@Controller('/v1/auth')
export class AuthController {

    constructor(
        private readonly signIn: AuthSignInUseCase,
        private readonly meUseCase: AuthMeUseCase
    ){}

    @Post('/sign-in')
    @HttpCode(200)
    async signInRoute(@Body() body, @Ip() ip, @Req() request: Request) {
        return await this.signIn.execute(body, ip, request.headers['user-agent'])
    }

    @Get('/me')
    async meRoute(@Operator() operator) {
        return this.meUseCase.execute(operator)
    }

}