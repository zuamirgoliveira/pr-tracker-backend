# PR Tracker Backend

Backend do projeto PR Tracker — um Micro-SaaS leve e direto ao ponto para monitorar Pull Requests no GitHub em tempo real.

---

## 🧠 Overview

O PR Tracker é um microserviço que atua como proxy entre o BFF (ou frontend) e a GitHub API, entregando **visibilidade em tempo real** sobre o fluxo de Pull Requests das squads.

Através de uma API REST simples, buscamos fornecer:

* Métricas de revisão (tempo médio, PRs estagnados)
* Alertas de gargalos via filtros de tempo
* Insights de fluxo contínuo
* Listagem de repositórios do usuário autenticado

Tudo isso **sem banco de dados local** — 100% orientado por integração direta com a GitHub API.

---

## 🎯 Objetivo do MVP

Nesta primeira versão, o PR Tracker Backend deve:

1. **Autenticar o usuário** via GitHub (OAuth2 Client)
2. **Expor `/api/v1/user`** para devolver dados do usuário autenticado
3. **Expor `/api/v1/user/repos`** para listar repositórios do usuário autenticado
4. **Expor `/api/v1/repos/{owner}/{repo}/pulls`** para listar Pull Requests de um repositório específico

    * Suportar filtros GitHub-native (`state`, `head`, `base`, `sort`, `direction`, `draft`, `since`)
    * Suportar filtros custom (`minHoursOpen`, `maxHoursOpen`, `author`, `label`)
5. **Calcular e retornar** para cada PR: título, URL, autor, data de criação e tempo aberto
6. **Manter o sistema stateless** — todo token GitHub vem via header `Authorization: Bearer <token>`

---

## 📦 Endpoints Disponíveis

| Método           | Rota                                              | Descrição                                                                               |
| ---------------- | ------------------------------------------------- | --------------------------------------------------------------------------------------- |
| GET              | `/api/v1/user`                                    | Dados do usuário autenticado (login, nome, avatar, email)                               |
| GET              | `/api/v1/user/repos`                              | Lista repositórios do usuário (filtros: `type`, `sort`, `direction`, `perPage`, `page`) |
| GET              | `/api/v1/orgs/{org}/repos`                        | Lista repositórios de uma organização                                                   |
| GET              | `/api/v1/repos/{owner}/{repo}/pulls`              | Lista Pull Requests de um repo com todos os filtros mencionados                         |
| (filtros custom) | `minHoursOpen`, `maxHoursOpen`, `author`, `label` | Aplicados após o fetch da GitHub API                                                    |

---

## 🧰 Stack

* **Java 17**
* **Spring Boot 3.4.5**
* **Maven** (com Maven Wrapper)
* **Spring Web & WebClient**
* **Spring Security** (OAuth2 Client)
* **Lombok** (para código mais enxuto)

---

## ▶️ Como Rodar o Projeto

1. Clone o repositório e entre na pasta:

   ```bash
   git clone https://github.com/seu-org/pr-tracker-backend.git
   cd pr-tracker-backend
   ```

2. Execute com Maven Wrapper:

   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=local
   ```

**Pré-requisitos**

* Java 17+
* (Opcional) Maven — mas você pode usar o wrapper incluído

---

## ⚙️ Configuração Local e OAuth2 (GitHub)

### 1. Registre um OAuth App no GitHub

Acesse: [https://github.com/settings/developers](https://github.com/settings/developers)

* **Application name**: PR Tracker Backend
* **Homepage URL**: `http://localhost:8080`
* **Authorization callback URL**: `http://localhost:8080/login/oauth2/code/github`

Anote o **Client ID** e o **Client Secret**.

### 2. Crie `application-local.yaml` (não versionado)

Em `src/main/resources/application-local.yaml`:

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          github:
            client-id:     SUA_CLIENT_ID_AQUI
            client-secret: SEU_CLIENT_SECRET_AQUI
            scope:         read:user,repo
            redirect-uri:  "{baseUrl}/login/oauth2/code/github"
            client-name:   GitHub
        provider:
          github:
            authorization-uri:  https://github.com/login/oauth/authorize
            token-uri:          https://github.com/login/oauth/access_token
            user-info-uri:      https://api.github.com/user
server:
  port: 8080
```

> Adicione este arquivo ao `.gitignore` para não comitar credenciais.

### 3. Ative o perfil `local`

* **Terminal**:

  ```bash
  ./mvnw spring-boot:run -Dspring-boot.run.profiles=local
  ```
* **IntelliJ IDEA**:
  Em **Run > Edit Configurations**, adicionar em **VM Options**:

  ```
  -Dspring.profiles.active=local
  ```

---

## 🏗️ Estrutura de Pastas

```
src/main/java/com/prtracker/backend
│
├── PrTrackerBackendApplication.java
│
├── config/
│   ├── SecurityConfig.java
│   └── GitHubWebClientConfig.java
│
├── exception/
│   ├── ErrorDto.java
│   └── GlobalExceptionHandler.java
│
├── github/
│   └── GitHubApiClient.java
│
├── user/
│   ├── UserController.java      // GET /api/v1/user
│   ├── UserService.java
│   └── UserDto.java
│
├── repository/
│   ├── RepositoryController.java   // /user/repos, /orgs/{org}/repos
│   ├── RepositoryService.java
│   ├── RepositoryFilter.java       // record de query-params
│   └── RepositoryDto.java          // record de resposta do GitHub
│
├── pullrequest/
│   ├── PullRequestController.java  // GET /api/v1/repos/{owner}/{repo}/pulls
│   ├── PullRequestService.java
│   ├── PullRequestFilter.java      // record de query-params custom
│   └── PullRequestDto.java         // record de resposta do GitHub
│
└── resources/
    ├── application.yaml
    └── application-local.yaml
```

---

## 👨‍💻 Author

Desenvolvido por **Zuma**
Engenheiro de Software, ciclista e entusiasta de arquitetura limpa.

---

## ⚖️ License

Este projeto está licenciado sob a **MIT License** — fique à vontade para usar, contribuir e evoluir.
