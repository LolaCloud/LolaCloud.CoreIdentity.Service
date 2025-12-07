import dotenv from 'dotenv';
import { get } from 'env-var';

dotenv.config()

export class EnvConfig {

    public static DATABASE_URL = get('DATABASE_URL').asString();
    public static ENCRYPTION_STRING = get('ENCRYPTION_STRING').required().asString()
    public static SALT_ROUNDS = get('SALT_ROUNDS').required().asInt();

}