import { HttpException, HttpStatus } from "@nestjs/common";

export class OperatorNotFoundException extends HttpException {

    constructor(payload?: object){
        super(payload ?? {message: 'Operator not found'}, HttpStatus.NOT_FOUND)
    }

}