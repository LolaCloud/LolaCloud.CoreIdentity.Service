<p align="center">
  <img src="./docs/assets/logo.png" alt="Lola Cloud Logo" width="150" />
</p>

<h1 align="center">Lola Cloud • Core Identity (LCI)</h1>

<p align="center">
  <strong>Autoridade Central de Identidade, Autenticação, RBAC e Sessões (Equivalente ao AWS IAM & Cognito)</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-25-ED8B00?style=flat-square&logo=openjdk&logoColor=white" alt="Java 25" />
  <img src="https://img.shields.io/badge/Spring_Boot-4.1.1-6DB33F?style=flat-square&logo=springboot&logoColor=white" alt="Spring Boot 4.1.1" />
  <img src="https://img.shields.io/badge/Spring_Security-7-6DB33F?style=flat-square&logo=springsecurity&logoColor=white" alt="Spring Security 7" />
  <img src="https://img.shields.io/badge/PostgreSQL-17-4169E1?style=flat-square&logo=postgresql&logoColor=white" alt="PostgreSQL" />
  <img src="https://img.shields.io/badge/Port-8080-blue?style=flat-square" alt="Port 8080" />
</p>

---

## 📌 Visão Geral

O **LCI (Lola Core Identity)** é o cérebro de segurança e controle de acesso da plataforma **Lola Cloud**. Ele atua como Identity Provider (IdP) e autoridade central de autenticação, emitindo tokens JWT criptografados com HMAC-SHA256 e fornecendo endpoints de introspecção interna consumidos pelos outros microserviços da plataforma (`runway`, `relate`, `store`, `journal`).

---

## 🛡️ Principais Responsabilidades

1. **Gestão de Operadores (IAM)**: Cadastro, bloqueio e auditoria de operadores da nuvem. Senhas são protegidas com hash criptográfico **BCrypt**.
2. **Autenticação & Sessões Ativas**: Controle de sessões com emissão de tokens JWT e capacidade de revogação remota imediata de qualquer sessão ativa.
3. **RBAC Granular (Role-Based Access Control)**:
   - Formato padronizado: `servico::recurso::acao`
   - Exemplos: `relate::instance::create`, `runway::dns::delete`, `store::bucket::list`
4. **Introspecção de Tokens (API Interna)**:
   - Endpoint: `POST /internal/v1/auth/introspect`
   - Permite que qualquer microsserviço da plataforma valide um token JWT de forma stateless ou via validação remota de sessão ativa.

---

## 📡 Principais Endpoints da API REST

| Método | Endpoint | Descrição |
| :--- | :--- | :--- |
| `POST` | `/api/v1/auth/login` | Autenticação com credenciais e emissão do JWT |
| `POST` | `/api/v1/auth/logout` | Revogação da sessão atual |
| `GET` | `/api/v1/operators` | Listagem de operadores cadastrados |
| `POST` | `/api/v1/operators` | Criação de novo operador com roles e permissões |
| `GET` | `/api/v1/sessions` | Listagem de sessões ativas do operador autenticado |
| `DELETE` | `/api/v1/sessions/{id}` | Encerramento/revogação remota de sessão |
| `POST` | `/internal/v1/auth/introspect` | API interna consumida por outros serviços para validação de JWT |

---

## 🛠️ Stack Tecnológica

- **Linguagem**: Java 25 (OpenJDK)
- **Framework**: Spring Boot 4.1.1
- **Segurança**: Spring Security 7 + JJWT (`io.jsonwebtoken:jjwt-api:0.12.6`)
- **Persistência**: Spring Data JPA / Hibernate 7
- **Migrations**: Flyway
- **Banco de Dados**: PostgreSQL 17 (`jdbc:postgresql://localhost:5433/lola`)

---

## 🚀 Como Executar

```bash
# Executar a aplicação
./mvnw spring-boot:run
```
Porta padrão: **`8080`**  
Operador inicial padrão: **`root`** / **`root123`**
