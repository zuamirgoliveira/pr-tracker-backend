# AGENTS.md Instructions

This document outlines the assumptions and conventions for any AI agent operating on the **PR Tracker Backend** repository.

---

## 1. General Context

* **Project**: PR Tracker Backend (proxy microservice for the GitHub API)
* **Description**: Stateless REST API exposing user, repository, pull request, commit, and branch data with no local database.
* **Tech Stack**: Java 17, Spring Boot 3.4.5, Maven (Wrapper), Spring Web MVC & WebClient (WebFlux), Spring Security (OAuth2 Client / Resource Server), Spring Validation, Spring Actuator, springdoc-openapi-starter-webmvc-ui, Lombok
* **Project Layout**: Feature-based packages (`user/`, `repository/`, `pullrequest/`, `commit/`, `branch/`, `github/`, `config/`, `exception/`)

---

## 2. MVP Goals

1. Authenticate users via GitHub (OAuth2 client) or Personal Access Token in the `Authorization` header.
2. Expose **`GET /api/v1/user`** → return authenticated user data.
3. Expose **`GET /api/v1/user/repos`** and **`GET /api/v1/users/{username}/repos`** → list user repositories (authenticated & public).
4. Expose **`GET /api/v1/orgs/{org}/repos`** → list organization repositories.
5. Expose **`GET /api/v1/repos/{owner}/{repo}/pulls`** → list pull requests for a repository:

   * Pass-through GitHub-native filters: `state`, `head`, `base`, `sort`, `direction`, `draft`, `since`.
   * Custom in-memory filters: `minHoursOpen`, `maxHoursOpen`, `author`, `label`.
6. Expose **`GET /api/v1/repos/{owner}/{repo}/commits`** → list commits with filters: `sha`, `path`, `author`, `committer`, `since`, `until`, `perPage`, `page`.
7. Expose **`GET /api/v1/repos/{owner}/{repo}/branches`** → list branches with filters: `protected`, `perPage`, `page`.
8. For each PR, calculate and return: `title`, `html_url`, `user.login`, `created_at`, and `hoursOpen`.
9. Maintain a **stateless** system: all tokens come via the `Authorization: Bearer <token>` header.

---

## 3. Package Structure (Feature-Based)

```text
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
│   ├── CommitController.java      // GET /api/v1/repos/{owner}/{repo}/commits
│   ├── CommitService.java
│   ├── CommitsFilter.java
│   └── CommitDto.java
└── branch/
    ├── BranchController.java      // GET /api/v1/repos/{owner}/{repo}/branches
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
* Validation: annotate DTOs with constraint annotations (`@Valid`, `@NotBlank`, etc.) and configure `@Validated` on controllers.
* Error Handling: use `@RestControllerAdvice` (`GlobalExceptionHandler`) and standardize with `ErrorDto { status, message }`.

---

## 5. Testing & Quality Gates

* **Test coverage**: 100% of classes covered via unit and slice tests.
* **JaCoCo** configured for **90%** minimum line coverage, enforced on `verify` phase.
* Tests use `@WebMvcTest` for controllers and `@ExtendWith(MockitoExtension.class)` for services and client.

---

## 6. Docker & Containerization

* **Dockerfile**: multi-stage build on Ubuntu 22.04, JDK + Maven for build, JRE headless for runtime.
* **Image build**: `docker build --no-cache -t pr-tracker-backend:local .`
* **Run**: `docker run --rm -d -p 8080:8080 --name pr-tracker-backend pr-tracker-backend:local`
* Optional: **docker-compose.yml** available for multi-service setups.

---

## 7. CI / GitHub Actions

* Workflow (`.github/workflows/ci.yml`) triggers on PR to `main`.
* Steps: checkout, setup JDK 17, cache Maven, `mvn clean verify` (includes coverage check), upload JaCoCo report, build & push Docker image.

---

> **Note:** Keep this `AGENTS.md` file at the repository root. Any AI agent should read it to ensure consistency with PR Tracker Backend standards.
