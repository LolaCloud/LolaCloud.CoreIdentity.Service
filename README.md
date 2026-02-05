# LolaCloud Core Identity Service (LCI) 🔐

## Visão Geral
O **Lola Core Identity Service** é a espinha dorsal de gerenciamento de identidade e acesso (IAM) da plataforma Lola. Ele é construído com NestJS, TypeScript e utiliza **Clean Architecture** para separar responsabilidades entre domínio, aplicação e infraestrutura.

## Arquitetura e Padrões
- **Domain‑Driven Design**: Entidades, DTOs e enums são colocados em `src/domain`. Não há dependências externas neste layer.
- **Use‑Case Services**: Lógica de negócio está em `src/application/usecases`. Cada caso de uso é uma classe com `execute()` e é injetado via NestJS.
- **Infrastructure**: Conexões de banco (`src/infrastructure/database`) e middlewares HTTP (`src/infrastructure/http`). O módulo `HttpModule` registra rotas, middlewares de autorização e de operador.
- **Injeção de Dependência**: NestJS fornece o container DI que facilita a troca de implementações (ex.: `RunwayService`).
- **Migrations**: TypeORM com `migrationsRun: true`; migrações ficam em `src/infrastructure/database/typeORM/migrations`.
- **Testing**: Testes unitários em `src/**/*.spec.ts` e e2e em `test/e2e`. Testes executados com Jest via `bun run test` ou `bun run test:e2e`.

## Padrões de Segurança
- **JWT** com identificador de sessão único (`sid`).
- **Bcrypt** para hashing de senhas; número de rounds definido por `SALT_ROUNDS`.
- **RBAC Wildcard**: Permissões no formato `service::resource::action` ou `service::resource::*`.
- **Audit Logging**: Eventos de segurança são emitidos para o Lola Keeper (não detalhado aqui).

## Bibliotecas e Tecnologias
| Stack | Utilização |
|-------|------------|
| **NestJS** | Framework web, suporte a módulos, pipes e filtros |
| **TypeScript** | Linguagem de tipagem estática |
| **TypeORM** | ORM PostgreSQL com suporte a migrations |
| **Bun** | Gerenciador de pacotes e runner (scripts npm equivalentes) |
| **Jest** | Testes unitários e e2e |
| **Prettier** | Formatação de código |
| **ESLint** | Linter com regras de estilo |
| **dotenv** | Carregamento de variáveis de ambiente |
| **axios** | Cliente HTTP (RunwayService) |

## Instalação e Execução
```bash
# Clone o repositório
git clone <repo-url>
cd Lola.CoreIdentity.Service

# Instale dependências (Bun ou npm)
bun install

# Copie o arquivo de exemplo de env
cp .env.example .env
# Ajuste as variáveis conforme necessário

# Rode migrations (já é executado na inicialização)
bun run typeorm migration:run

# Desenvolvimento
bun run start:dev

# Produção
bun run build
bun run start
```

## Scripts Disponíveis
| Script | Descrição |
|--------|------------|
| `bun run build` | Compila TS para `dist/` |
| `bun run start` | Executa a build em produção |
| `bun run start:dev` | Executa em modo watch |
| `bun run lint` | Roda ESLint e corrige automaticamente |
| `bun run format` | Formata arquivos com Prettier |
| `bun run test` | Executa testes unitários |
| `bun run test:e2e` | Executa testes de ponta a ponta |
| `bun run test:e2e <file>` | Executa um arquivo de teste e2e específico |
| `bun run typeorm migration:generate <name>` | Cria nova migration |
| `bun run typeorm migration:run` | Aplica migrations pendentes |
| `bun run typeorm migration:revert` | Reverte última migration |

## Rotas Principais
- `/v1/auth/**` – login, logout, token refresh, etc. |
- `/v1/operator/**` – CRUD de operadores e gestão de permissões |
- `/v1/runway/**` – Integrações com serviço externo (Runway) |
- `/v1/health` – endpoint de verificação de saúde |

## Variáveis de Ambiente
```dotenv
APPLICATION_PORT=3000
DATABASE_URL=postgres://lola:lola@localhost:5432/main
ENCRYPTION_STRING=YOUR_SECRET
SALT_ROUNDS=5
LOLA_MICROSERVICE_SECRET=YOUR_SECRET
```

## Como Contribuir
1. Faça fork e crie uma branch feature. |
2. Siga os padrões de código (Prettier + ESLint). |
3. Crie tests que cubram novas funcionalidades. |
4. Abra um Pull Request. |

## Documentação Adicional
A documentação OpenAPI está disponível em `/api` quando o servidor está rodando. |

## Suporte
Para dúvidas ou issues, abra um ticket no GitHub. |
