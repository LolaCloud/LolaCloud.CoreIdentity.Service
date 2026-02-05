import { Injectable } from "@nestjs/common";
import snakeCaseKeys from 'snakecase-keys'
import { RunwayService } from "src/services/runway/runway.service";

@Injectable()
export class GetAllCloudflareAccountsUseCase {

    constructor(
        private readonly runwayService: RunwayService
    ){}

    async execute() {
        const accounts = await this.runwayService.getAllAccounts();

        return {
            accounts: snakeCaseKeys(accounts.accounts, {deep: true})
        }
    }

}
