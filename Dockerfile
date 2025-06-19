# Stage 1: Build on Ubuntu
FROM ubuntu:22.04 AS build

# Evita prompts do apt
ENV DEBIAN_FRONTEND=noninteractive

# Instala JDK 17, Maven e dependências
RUN apt-get update \
  && apt-get install -y --no-install-recommends \
     openjdk-17-jdk \
     maven \
  && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copia apenas o pom e o wrapper para cache de dependências
COPY pom.xml mvnw ./
COPY .mvn .mvn

# Pré-carrega dependências
RUN chmod +x mvnw \
  && ./mvnw dependency:go-offline -B

# Copia o código e gera o jar
COPY src src
RUN ./mvnw clean package -DskipTests -B

# Stage 2: Runtime on Ubuntu
FROM ubuntu:22.04

# Evita prompts do apt
ENV DEBIAN_FRONTEND=noninteractive

# Só precisa do JRE para rodar o jar
RUN apt-get update \
  && apt-get install -y --no-install-recommends \
     openjdk-17-jre-headless \
  && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copia o jar gerado no build
COPY --from=build /app/target/*SNAPSHOT.jar app.jar

# Porta exposta pelo Spring Boot
EXPOSE 8080

# Comando padrão
ENTRYPOINT ["java", "-jar", "/app/app.jar"]