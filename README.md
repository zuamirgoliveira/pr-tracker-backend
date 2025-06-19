# PR Tracker Backend

Backend do projeto **PR Tracker** — um microserviço leve e direto ao ponto para monitorar Pull Requests no GitHub em tempo real.

---

## 🧠 Overview

O PR Tracker Backend é uma API REST **stateless** que atua como proxy entre o BFF/frontend e a GitHub API, entregando:

* **Visibilidade em tempo real** sobre Pull Requests, commits e branches
* **Métricas de revisão**: tempo médio aberto, PRs estagnados
* **Filtros avançados** (GitHub-native e custom)
* **Comparação de branches** com a `main`

Não há banco de dados local — todos os dados vêm diretamente da GitHub API.

---

## 🎯 Objetivo do MVP

1. **Autenticar** usuário via GitHub (OAuth2 Client) ou Personal Access Token
2. **Expor** `GET /api/v1/user` → dados do usuário autenticado
3. **Expor** `GET /api/v1/user/repos` e `GET /api/v1/orgs/{org}/repos` → listar repositórios
4. **Expor** `GET /api/v1/repos/{owner}/{repo}/pulls` → listar Pull Requests
5. **Expor** `GET /api/v1/repos/{owner}/{repo}/commits` → listar commits
6. **Expor** `GET /api/v1/repos/{owner}/{repo}/branches` → listar branches
7. **Calcular e retornar** para cada PR: título, URL, autor, data de criação e tempo aberto
8. **Manter o sistema stateless**: todo token GitHub chega via header `Authorization: Bearer <token>`

---

## 📦 Endpoints Disponíveis

| Método  | Rota                                    | Descrição                                                                                                                                                |
| ------- | --------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **GET** | `/api/v1/user`                          | Dados do usuário autenticado (`login`, `name`, `avatarUrl`, `email`)                                                                                     |
| **GET** | `/api/v1/user/repos`                    | Repositórios do usuário autenticado (filtros: `type`, `sort`, `direction`, `perPage`, `page`)                                                            |
| **GET** | `/api/v1/users/{username}/repos`        | Repositórios públicos por usuário (filtros: `type`, `sort`, `direction`, `perPage`, `page`)                                                              |
| **GET** | `/api/v1/orgs/{org}/repos`              | Repositórios de uma organização (mesmos filtros acima)                                                                                                   |
| **GET** | `/api/v1/repos/{owner}/{repo}/pulls`    | Pull Requests (GitHub-native: `state`, `head`, `base`, `sort`, `direction`, `draft`, `since`; custom: `minHoursOpen`, `maxHoursOpen`, `author`, `label`) |
| **GET** | `/api/v1/repos/{owner}/{repo}/commits`  | Commits (filtros: `sha`, `path`, `author`, `committer`, `since`, `until`, `perPage`, `page`)                                                             |
| **GET** | `/api/v1/repos/{owner}/{repo}/branches` | Branches (filtros: `protected`, `perPage`, `page`)                                                                                                       |

---

## 🧰 Stack

* **Java 17**
* **Spring Boot 3.4.5**
* **Maven** (com Maven Wrapper)
* **Spring Web MVC & WebClient (WebFlux)**
* **Spring Security** (OAuth2 Client / Resource Server)
* **Spring Validation** (`spring-boot-starter-validation`)
* **Spring Actuator** (`spring-boot-starter-actuator`)
* **springdoc-openapi-starter-webmvc-ui**
* **Lombok**

---

## ▶️ Como Rodar o Projeto

1. Clone o repositório:

   ```bash
   git clone https://github.com/seu-org/pr-tracker-backend.git
   cd pr-tracker-backend
   ```
2. Execute com o Maven Wrapper e perfil `local`:

   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=local
   ```

**Pré-requisitos:** Java 17+ (use o wrapper para o Maven)

---

## ⚙️ Configuração Local e OAuth2 (GitHub)

1. Registre um OAuth App em GitHub:

   * **Application name**: PR Tracker Backend
   * **Homepage URL**: `http://localhost:8080`
   * **Authorization callback URL**: `http://localhost:8080/login/oauth2/code/github`

2. Crie `src/main/resources/application-local.yaml` (não versionado):

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
        provider:
          github:
            authorization-uri: https://github.com/login/oauth/authorize
            token-uri:         https://api.github.com/login/oauth/access_token
            user-info-uri:     https://api.github.com/user
server:
  port: 8080
```

> **IMPORTANTE:** adicione ao `.gitignore`.

---

## 🏗️ Estrutura de Pastas

```text
src/main/java/com/prtracker/backend
├── BackendApplication.java
├── config/
│   ├── SecurityConfig.java
│   └── GitHubWebClientConfig.java
├── exception/
│   ├── ErrorDto.java
│   └── GlobalExceptionHandler.java
├── github/
│   └── GitHubApiClient.java
├── user/
│   ├── UserController.java
│   ├── UserService.java
│   └── UserDto.java
├── repository/
│   ├── RepositoryController.java
│   ├── RepositoryService.java
│   ├── RepositoryFilter.java
│   └── RepositoryDto.java
├── pullrequest/
│   ├── PullRequestController.java
│   ├── PullRequestService.java
│   ├── PullRequestFilter.java
│   └── PullRequestDto.java
├── commit/
│   ├── CommitController.java
│   ├── CommitService.java
│   ├── CommitsFilter.java
│   └── CommitDto.java
└── branch/
    ├── BranchController.java
    ├── BranchService.java
    ├── BranchFilter.java
    └── BranchDto.java

resources/
├── application.yaml
└── application-local.yaml
```

---

## 📋 Testes e Cobertura

* Implementados testes unitários e de slice (controllers) cobrindo **100%** das classes.
* **JaCoCo** configurado para mínimo de **90%** de cobertura de linhas durante o build Maven.

```xml
<!-- jacoco-maven-plugin no pom.xml -->
<plugin>
  <groupId>org.jacoco</groupId>
  <artifactId>jacoco-maven-plugin</artifactId>
  <version>0.8.10</version>
  <executions>
    <execution><goals><goal>prepare-agent</goal></goals></execution>
    <execution><id>report</id><phase>verify</phase><goals><goal>report</goal></goals></execution>
    <execution><id>check</id><goals><goal>check</goal></goals>
      <configuration>
        <rules>
          <rule>
            <element>BUNDLE</element>
            <limits>
              <limit><counter>LINE</counter><value>COVEREDRATIO</value><minimum>0.90</minimum></limit>
            </limits>
          </rule>
        </rules>
      </configuration>
    </execution>
  </executions>
</plugin>
```

---

## 🐳 Docker

### Dockerfile (multi-stage com Ubuntu)

**Build da imagem**:

```bash
docker build --no-cache -t pr-tracker-backend:local .
```

**Rodar o container**:

```bash
docker run --rm -d -p 8080:8080 --name pr-tracker-backend pr-tracker-backend:local
```

**Health check**:

```bash
curl -i http://localhost:8080/actuator/health
```

> **Dica:** use `hadolint Dockerfile` para lint e `docker scan pr-tracker-backend:local` ou `trivy` para vulnerabilidades.

---

## 🔄 CI com GitHub Actions

Arquivo: `.github/workflows/ci.yml`

---

## 👨‍💻 Author

Desenvolvido por **Zuma**
Engenheiro de Software, ciclista e entusiasta de arquitetura limpa.

---

## ⚖️ License

Este projeto está licenciado sob a **MIT License** — fique à vontade para usar, contribuir e evoluir.
