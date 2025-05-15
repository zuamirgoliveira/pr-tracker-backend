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

## 👨‍💻 Author

Desenvolvido por **Zuma Gutem**  
Engenheiro de Software, ciclista e entusiasta de arquitetura limpa.  
[LinkedIn](https://www.linkedin.com/in/seu-perfil)

---

## ⚖️ License

Este projeto está licenciado sob a **MIT License** — sinta-se livre para usar, contribuir e evoluir.
