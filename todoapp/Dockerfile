# Home-Assistant-Add-on-Image: Backend, Frontend und PostgreSQL in einem Container.
# Für den normalen Docker-Betrieb weiterhin docker-compose.yml (3 separate Container) verwenden.
#
# Der Build-Kontext eines HA-Add-ons ist auf diesen Ordner (todoapp/) beschränkt,
# daher wird der Quellcode hier per git clone aus dem öffentlichen Repo geholt,
# statt ihn wie im normalen docker-compose-Setup direkt aus dem Repo-Root zu kopieren.
ARG REPO_URL=https://github.com/vikried/todo-app.git
ARG REPO_REF=main

# ---- Backend bauen ----
FROM maven:3.9-eclipse-temurin-17 AS backend-build
ARG REPO_URL
ARG REPO_REF
RUN apt-get update && apt-get install -y --no-install-recommends git ca-certificates \
    && rm -rf /var/lib/apt/lists/*
RUN git clone --depth 1 --branch "${REPO_REF}" "${REPO_URL}" /src
WORKDIR /src/backend
RUN mvn clean package -DskipTests

# ---- Frontend bauen ----
FROM node:20 AS frontend-build
ARG REPO_URL
ARG REPO_REF
RUN apt-get update && apt-get install -y --no-install-recommends git ca-certificates \
    && rm -rf /var/lib/apt/lists/*
RUN git clone --depth 1 --branch "${REPO_REF}" "${REPO_URL}" /src
WORKDIR /src/frontend
RUN npm install
RUN npm run build

# ---- Laufzeit-Image ----
FROM debian:bookworm-slim

RUN apt-get update && apt-get install -y --no-install-recommends \
        openjdk-17-jre-headless \
        postgresql \
        nginx \
        jq \
        gosu \
    && rm -rf /var/lib/apt/lists/*

COPY --from=backend-build /src/backend/target/*.jar /app/backend.jar
COPY --from=frontend-build /src/frontend/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/sites-enabled/default
COPY run.sh /run.sh
RUN chmod +x /run.sh

EXPOSE 80
ENTRYPOINT ["/run.sh"]
