# Home-Assistant-Add-on-Image: Backend, Frontend und PostgreSQL in einem Container.
# Für den normalen Docker-Betrieb weiterhin docker-compose.yml (3 separate Container) verwenden.

# ---- Backend bauen ----
FROM maven:3.9-eclipse-temurin-17 AS backend-build
WORKDIR /app
COPY backend/pom.xml .
COPY backend/src ./src
RUN mvn clean package -DskipTests

# ---- Frontend bauen ----
FROM node:20 AS frontend-build
WORKDIR /app
COPY frontend/package*.json ./
RUN npm install
COPY frontend/. .
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

COPY --from=backend-build /app/target/*.jar /app/backend.jar
COPY --from=frontend-build /app/dist /usr/share/nginx/html
COPY ha-addon/nginx.conf /etc/nginx/sites-enabled/default
COPY ha-addon/run.sh /run.sh
RUN chmod +x /run.sh

EXPOSE 80
ENTRYPOINT ["/run.sh"]
