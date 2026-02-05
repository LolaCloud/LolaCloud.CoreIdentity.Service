# Lola Core Identity Service – Informações Técnicas

## Visão geral
O **Lola Core Identity Service (LCI)** é o serviço central de gerenciamento de identidade e acesso (IAM) da plataforma Lola. Ele fornece autenticação, autorização, gestão de sessões e controle de permissões granular em uma arquitetura limpa, testável e altamente escalável.

> O objetivo deste documento é oferecer ao frontend todas as informações necessárias para gerar uma página explicativa completa sobre os recursos, endpoints e arquitetura do serviço.

## Estrutura do repositório
```
├─ src/
│  ├─ application/       # Use‑cases (lógica de negócio)
│  ├─ domain/            # Entidades, DTOs, enums
│  ├─ infrastructure/    # Modulos NestJS, banco de dados, middlewares
│  │  ├─ database/
│  │  │  ├─ typeORM/
│  │  │  │  ├─ data-source.ts
│  │  │  │  ├─ entities/
│  │  │  │  │  ├─ operator.entity.ts
│  │  │  │  │  ├─ permission.entity.ts
│  │  │  │  │  ├─ session.entity.ts
│  │  │  │  ├─ migrations/
│  │  └─ http/
│  ├─ services/          # Serviços externos (Runway, etc.)
│  │  └─ runway/
│  ├─ main.ts
│  └─ config.ts
├─ test/
│  ├─ e2e/
│  └─ unit/
├─ package.json
├─ tsconfig.json
└─ README.md
```

## Principais componentes
### Domain
- **Operator**: Usuário do sistema, com permissões e sessões.
- **Permission**: Regra de acesso baseada em `service::resource::action`.
- **Session**: Representa um token JWT ativo; armazena IP, agente de usuário, dispositivo, localização e expiração.

### Application (Use‑Cases)
- **Auth**: Sign‑in, sign‑out, token refresh, obter sessões, desativar sessão.
- **Operator**: CRUD de operadores, atualizar senha, atribuir permissões.
- **Runway**: Integração com o serviço externo Runway (gerenciamento de contas Cloudflare, zonas, registros DNS, etc.).

### Infrastructure
- **DatabaseModule**: Configuração do TypeORM e data source.
- **HttpModule**: Middlewares de autorização e de operador.
- **RunwayModule**: Endpoint `/v1/runway` exposto via `RunwayController`.

## Autenticação e Sessão
- Autenticação por **JWT**. O token contém: `operatorId`, `permissions`, `sid` (session id).
- Ao entrar (`POST /v1/auth/sign-in`), um **Session** é criado no banco.
- As sessões são invalidadas via `DELETE /v1/auth/sessions/:id` ou `POST /v1/auth/disable-session`.
- Cada sessão possui `expiresAt`; expiradas são removidas via job cron.

## Autorização
- Middleware `AuthorizationMiddleware` verifica a presença e validade do JWT.
- Middleware `OperatorMiddleware` carrega o operador e suas permissões.
- Permissões são definidas em `Permission` e podem usar *wildcards* (ex.: `lci::operator::*`).
- O endpoint `GET /v1/operators` está protegido por `lci::operator::read`.

## Endpoints Principais
| Método | Rota | Descrição | Autorização | Payload | Resposta |
|--------|------|-----------|------------|---------|---------|
| POST | /v1/auth/sign-in | Autentica operador | Nenhuma | `{ username, password }` | `{ token, sessionId }` |
| POST | /v1/auth/refresh | Renova token | JWT válido | `{ refreshToken }` | `{ token }` |
| GET | /v1/operators | Lista operadores | `lci::operator::read` | | Array de operadores |
| POST | /v1/operators | Cria operador | `lci::operator::create` | `{ username, password, name, email }` | Operador criado |
| PUT | /v1/operators/:id | Atualiza operador | `lci::operator::update` | `{ name?, email? }` | Operador atualizado |
| DELETE | /v1/operators/:id | Exclui operador | `lci::operator::delete` | | 204 |
| GET | /v1/runway/accounts | Lista contas Runway | `runway::account::read` | | Array de contas |
| POST | /v1/runway/accounts | Cria conta Runway | `runway::account::create` | `{ name, email }` | Conta criada |
| POST | /v1/runway/zones | Cria zona | `runway::zone::create` | `{ accountId, zoneName }` | Zona criada |
| GET | /v1/runway/zones | Lista zonas | `runway::zone::read` | | Array de zonas |
| POST | /v1/runway/dns-records | Cria registro DNS | `runway::dns-record::create` | `{ zoneId, recordType, name, content }` | Registro criado |
| DELETE | /v1/runway/dns-records/:id | Remove registro | `runway::dns-record::delete` | | 204 |

## Migrations
- A aplicação aplica automaticamente migrações ao iniciar (`migrationsRun: true`).
- Scripts de migração localizados em `src/infrastructure/database/typeORM/migrations/`.
- Para criar nova migração: `bun run typeorm migration:generate <nome>`.

## Testes
- **Unitários**: `src/**/*.spec.ts` executados com `bun run test`.
- **E2E**: `test/e2e/**/*.spec.ts` executados com `bun run test:e2e`.
- **Cobertura**: `bun run test:cov`.

## Build e Deploy
- Build: `bun run build` (gera `dist/`).
- Iniciar: `bun run start` (produção) ou `bun run start:dev` (dev).
- Dockerfile e Kubernetes manifests não incluídos neste repositório.

## Monitoramento e Logs
- Utiliza middlewares de logging de NestJS. Logs de autenticação e autorização são gravados em nível de aplicação.
- Sessões expiram automaticamente; logs de encerramento de sessão são registrados.

## Segurança
- **CSRF**: Protegido por tokens de sessão; endpoints de escrita exigem `Authorization` header.
- **Rate Limiting**: Implementado via middleware customizado (não detalhado aqui).
- **CORS**: Configurado para permitir apenas domínios autorizados.
- **Salts**: Bcrypt com `SALT_ROUNDS` definido em `.env`.
- **Segredo**: `LOLA_MICROSERVICE_SECRET` usado em integração Runway; nunca exposto no código.

## Documentação adicional
- **Swagger**: Disponível em `/api` quando o servidor está rodando.
- **OpenAPI**: Auto‑gerado pelo NestJS; pode ser exportado em JSON.

## Fluxo típico de autenticação
1. **Sign‑in** (`POST /v1/auth/sign-in`): Recebe username e password.
2. Verifica credenciais contra `Operator.password` (hash bcrypt).
3. Se válido, cria `Session` no banco.
4. Emite JWT contendo `operatorId`, `permissions`, `sid` e `exp`.
5. Cliente armazena token e `sessionId`.
6. Em cada requisição subsequente, cliente envia `Authorization: Bearer <token>`.
7. Middleware valida token, carrega operador, verifica permissão e permite acesso.

## Fluxo de autorização por permissões
- Cada operador possui um array de `Permission`.
- As permissões são strings no formato `service::resource::action`.
- O middleware `AuthorizationMiddleware` extrai as permissões do token e verifica se a permissão requisitada está presente.
- Wildcards (`*`) permitem concessão de permissões genéricas.

## Integração Runway
- O serviço Runway roda em `http://localhost:3001` (configurável via `RUNWAY_URL`).
- `RunwayService` encapsula todas as chamadas REST, adicionando o header `Authorization`.
- Casos de uso relacionados: `GetAllAccountsUseCase`, `CreateCloudflareAccountUseCase`, `AddZoneUseCase`, `GetZonesFromCloudflareUseCase`, `AddDNSRecordUseCase`, etc.
- Endpoints protegidos com permissões `runway::account::*`, `runway::zone::*`, `runway::dns-record::*`.

## Checklist para o Frontend
- Listar endpoints principais e requisitos de autorização.
- Expor fluxos de autenticação e sessão.
- Mapear permissões e suas descrições.
- Incluir exemplos de payloads e respostas JSON.
- Documentar estrutura de dados do `Operator`, `Permission`, `Session`.
- Mostrar workflow de criação de conta Runway, zona e registro DNS.
- Incluir links para Swagger/OpenAPI e para o repositório GitHub.
- Incluir instruções de build e deploy para ambientes de teste e produção.

---
