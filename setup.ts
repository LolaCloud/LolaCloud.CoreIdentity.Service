import { Client } from 'pg'
import { EnvConfig } from 'src/configuration';

async function setupSchemas() {
    const client = new Client({
        connectionString: EnvConfig.DATABASE_URL
    });
    client.connect();
    await client.query(`CREATE SCHEMA IF NOT EXISTS lci;`)
    await client.end()
}

async function setup() {
    await setupSchemas();
}

setup();
