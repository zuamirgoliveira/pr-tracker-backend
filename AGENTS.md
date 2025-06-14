# AGENTS.md Instructions

This document outlines the assumptions and conventions for any AI agent operating on the **PR Tracker Backend** repository.

---

## 1. General Context

* **Project**: PR Tracker Backend (proxy microservice for the GitHub API)
* **Description**: Stateless REST API exposing user, repository, pull request, commit, and branch data with no local database.
* **Tech Stack**: Java 17, Spring Boot 3.4.5, Maven (Wrapper), Spring Web MVC & WebClient, Spring Security (OAuth2 Client / Resource Server), Spring Validation, Spring Actuator, Springdoc OpenAPI UI, Lombok
* **Project Layout**: Feature-based packages (`user/`, `repository/`, `pullrequest/`, `commit/`, `branch/`, `github/`, `config/`, `exception/`)

---

## 2. MVP Goals

1. Authenticate users via GitHub (OAuth2 client) or Personal Access Token in the `Authorization` header.
2. Expose **GET /api/v1/user** → return authenticated user data.
3. Expose **GET /api/v1/user/repos** and **GET /api/v1/users/{username}/repos** → list user repositories (authenticated & public).
4. Expose **GET /api/v1/orgs/{org}/repos** → list organization repositories.
5. Expose **GET /api/v1/repos/{owner}/{repo}/pulls** → list pull requests for a repository:

   * Pass-through GitHub-native filters: `state`, `head`, `base`, `sort`, `direction`, `draft`, `since`.
   * Custom in-memory filters: `minHoursOpen`, `maxHoursOpen`, `author`, `label`.
6. Expose **GET /api/v1/repos/{owner}/{repo}/commits** → list commits with filters: `sha`, `path`, `author`, `committer`, `since`, `until`, `perPage`, `page`.
7. Expose **GET /api/v1/repos/{owner}/{repo}/branches** → list branches with filters: `protected`, `perPage`, `page`.
8. For each PR, calculate and return: `title`, `html_url`, `user.login`, `created_at`, and `hoursOpen`.
9. Maintain a **stateless** system: all tokens come via the `Authorization: Bearer <token>` header.

---

## 3. Package Structure (Feature-Based)

```
com.prtracker.backend
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
│   ├── UserController.java       // GET /api/v1/user
│   ├── UserService.java
│   └── UserDto.java
├── repository/
│   ├── RepositoryController.java // GET /api/v1/user/repos, GET /api/v1/users/{username}/repos, GET /api/v1/orgs/{org}/repos
│   ├── RepositoryService.java
│   ├── RepositoryFilter.java
│   └── RepositoryDto.java
├── pullrequest/
│   ├── PullRequestController.java // GET /api/v1/repos/{owner}/{repo}/pulls
│   ├── PullRequestService.java
│   ├── PullRequestFilter.java
│   └── PullRequestDto.java
├── commit/
│   ├── CommitController.java     // GET /api/v1/repos/{owner}/{repo}/commits
│   ├── CommitService.java
│   ├── CommitsFilter.java
│   ├── CommitDto.java
│   ├── CommitInfo.java
│   ├── CommitAuthor.java
│   └── GitHubBasicUserDto.java
└── branch/
    ├── BranchController.java     // GET /api/v1/repos/{owner}/{repo}/branches
    ├── BranchService.java
    ├── BranchFilter.java
    └── BranchDto.java

resources/
├── application.yaml
└── application-local.yaml
```

---

## 4. Code Conventions

* Use `record` for immutable DTOs and filter objects.
* Controllers: annotate with `@RestController` and class-level `@RequestMapping`; use `@ModelAttribute` for query params and `@RequestHeader("Authorization")` for tokens.
* Services: encapsulate calls to `GitHubApiClient` and block on reactive streams.
* Client: single `WebClient` configured in `GitHubWebClientConfig`.
* Validation: annotate DTOs with `@Valid` and use constraint annotations (`@NotBlank`, `@Size`, etc.).
* Error Handling: use `@RestControllerAdvice` with `GlobalExceptionHandler` and `ErrorDto`.

---

## 5. Authentication

* **Header**: `Authorization: Bearer <token>`.
* **Current flow**: Personal Access Token (PAT).
* **Future flow**: OAuth2 Authorization Code (single OAuth App for all clients).
* Abstract via a `TokenProvider` interface to switch implementations.

---

## 6. Error Handling

* Use `@RestControllerAdvice` in `GlobalExceptionHandler`.
* Handle `WebClientResponseException` and generic `Exception`.
* Standardize error responses with `ErrorDto { status, message }`.

---

## 7. How to Run

1. Configure credentials in `application-local.yaml` (Client ID/Secret).
2. Run with local profile:

   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=local
   ```
3. Smoke-test endpoints: `/actuator/health`, `/api/v1/user`.

---

## 8. Best Practices

* Group responsibilities by **feature**: controllers, services, and clients are organized within their respective feature packages.
* Validate DTOs using `@Validated` where needed.
* Document endpoints with OpenAPI/Swagger (springdoc-openapi-starter-webmvc-ui).

---

## 9. Dependencies (from pom.xml)

* `org.springframework.boot:spring-boot-starter-oauth2-client`
* `org.springframework.boot:spring-boot-starter-web`
* `org.springframework.boot:spring-boot-starter-webflux`
* `org.springframework.boot:spring-boot-devtools` (runtime, optional)
* `org.projectlombok:lombok` (optional)
* `org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.6`
* `org.springframework.boot:spring-boot-starter-validation`
* `org.springframework.boot:spring-boot-starter-actuator`
* `org.springframework.boot:spring-boot-starter-test` (test scope)
* `io.projectreactor:reactor-test` (test scope)
* `org.springframework.security:spring-security-test` (test scope)

---

> **Note:** Keep this `AGENTS.md` file at the repository root. Any AI agent integrated with this repo should read it first to ensure consistency and adherence to the PR Tracker Backend standards.
