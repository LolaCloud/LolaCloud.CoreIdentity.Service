# LolaCloud Core Identity Service (LCI) 🔐

O Lola Core Identity (LCI) é o serviço central de Gerenciamento de Identidade e Acesso (IAM) da plataforma Lola. Ele é responsável por garantir a segurança, autenticação e autorização granular em todo o ecossistema PaaS, servindo como a "fonte da verdade" para operadores e permissões.

## 🚀 Diferenciais Técnicos

* **Arquitetura Limpa (Clean Architecture)**: Separação rigorosa entre regras de domínio, casos de uso e detalhes de infraestrutura.

* **RBAC com Wildcards**: Sistema de permissões dinâmico inspirado no AWS IAM (servico::recurso::*).

* **Gestão de Sessões Ativas**: Rastreamento de dispositivos, endereços IP e revogação de sessões em tempo real.

* **Pronto para Auditoria**: Design focado em emitir eventos de segurança para o Lola Keeper.

* **TDD (Test-Driven Development)**: Cobertura de testes E2E para fluxos críticos de autenticação e gestão de operadores.

## 🛠️ Tech Stack

* **Framework**: NestJS

* **Linguagem**: TypeScript

* **Runtime/Package Manager**: Bun

* **ORM**: TypeORM

* **Banco de Dados**: PostgreSQL

* **Segurança**: JWT (JSON Web Tokens) com identificadores de sessão únicos (sid)


## 🔐 Sistema de Permissões (IAM)

O LCI utiliza uma lógica de seletor granular. Exemplos de permissões suportadas:

* **lci::operator::create**: Permissão específica para criar operadores.

* **lci::operator::***: Permissão para qualquer ação no módulo de operadores.

## 🚀 Como Iniciar

### Pré-requisitos

* Bun instalado (ou Node.js)

* Instância do PostgreSQL rodando

### Instalação

#### Clone o repositório e instale as dependências:

* `bun install`

Configure as variáveis de ambiente:

* `cp .env.example .env`


Execute as migrações do banco de dados:

* `bun run typeorm migration:run`


## Execução

### Modo desenvolvimento
* `bun run start:dev`

### Build de produção
* `bun run build`


## 🧪 Testes (TDD)

O projeto prioriza testes de ponta a ponta (E2E) para garantir a integridade da API:

### Executar todos os testes E2E
* `bun run test:e2e`

### Executar um teste específico (ex: Auth)
* `bun run test:e2e test/e2e/v1/auth/sign-in/post.spec.ts`