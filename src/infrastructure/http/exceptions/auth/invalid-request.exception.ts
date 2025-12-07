import { HttpException, HttpStatus } from "@nestjs/common";

export class InvalidRequestException extends HttpException {

    constructor(payload?: object){
        super(payload ?? {message: 'Invalid requests'}, HttpStatus.BAD_REQUEST)
    }

}