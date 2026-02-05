import { HttpException, HttpStatus } from "@nestjs/common";

export class InvalidCredentialsException extends HttpException {

    constructor(payload?: object){
        super(payload ?? {message: 'Invalid credentials'}, HttpStatus.UNAUTHORIZED)
    }

}
