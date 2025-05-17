# PR Tracker Backend

Backend do projeto PR Tracker — um Micro-SaaS leve e direto ao ponto para monitorar Pull Requests no GitHub em tempo real.

---

## 🧠 Overview

O PR Tracker é uma aplicação pensada para squads de desenvolvimento que querem **maior visibilidade sobre o fluxo de PRs** durante as sprints.

Através de uma API simples, conectada diretamente ao GitHub, buscamos entregar **métricas de revisão, alertas de gargalos e insights de fluxo contínuo** — tudo sem fricção.

---

## 🎯 Objetivo do MVP

Nesta primeira versão, o objetivo é:

- Autenticar o usuário via GitHub (OAuth2)
- Listar repositórios do usuário autenticado
- Buscar Pull Requests de um repositório
- Exibir status, autor, data de criação e tempo aberto
- Estruturar uma API REST para consumo futuro pelo frontend

Tudo isso **sem banco de dados local** — o sistema será 100% orientado por integração direta com a GitHub API.

---

## 🤔 Por que escolhemos o GitHub como repositório inicial?

- É amplamente utilizado por desenvolvedores e equipes modernas
- Possui uma API pública robusta, bem documentada e com suporte OAuth2
- Permite validar rapidamente a proposta de valor do produto
- Facilita integração futura com GitHub Actions e métricas de CI/CD

Outras plataformas como GitLab e Azure DevOps poderão ser integradas futuramente, com base no feedback dos usuários.

---

## 🧠 Stack

- Java 17
- Spring Boot 3.4.5
- Maven (with Maven Wrapper)
- Spring Web & WebClient
- Spring Security (OAuth2 Client)
- Lombok (for cleaner code)

---

## ▶️ How to Run the Project

```bash
./mvnw spring-boot:run
```

**Pré-requisitos:**
- Java 17+
- Maven (ou use o wrapper incluído)

---

## ⚙️ Configuração local e execução com OAuth2 (GitHub)

Para rodar o projeto localmente com autenticação via GitHub, siga os passos abaixo:

### 1. Crie uma OAuth App no GitHub

Acesse: [https://github.com/settings/developers](https://github.com/settings/developers)

- **Application name**: PR Tracker (ou similar)
- **Homepage URL**: `http://localhost:8080`
- **Authorization callback URL**: `http://localhost:8080/login/oauth2/code/github`

Após criar, salve o `Client ID` e `Client Secret`.

---

### 2. Crie o arquivo `application-local.yaml` (não versionado)

Local: `src/main/resources/application-local.yaml`

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          github:
            client-id: SUA_CLIENT_ID_AQUI
            client-secret: SEU_CLIENT_SECRET_AQUI
            scope: read:user,repo
            redirect-uri: "{baseUrl}/login/oauth2/code/github"
            client-name: GitHub
        provider:
          github:
            authorization-uri: https://github.com/login/oauth/authorize
            token-uri: https://github.com/login/oauth/access_token
            user-info-uri: https://api.github.com/user
```

Adicione esse arquivo ao `.gitignore` para evitar versionamento.

---

### 3. Execute com o perfil `local`

#### Terminal
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

#### IntelliJ IDEA
1. Vá em: `Run > Edit Configurations`
2. Em **VM Options**, adicione:
```
-Dspring.profiles.active=local
```

---

Agora a aplicação irá autenticar via GitHub e redirecionar para o endpoint `/api/v1/users/me` com os dados do usuário autenticado.

---

## 👨‍💻 Author

Desenvolvido por **Zuma Gutem**  
Engenheiro de Software, ciclista e entusiasta de arquitetura limpa.  
[LinkedIn](https://www.linkedin.com/in/seu-perfil)

---

## ⚖️ License

Este projeto está licenciado sob a **MIT License** — sinta-se livre para usar, contribuir e evoluir.
