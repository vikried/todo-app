# ---- STAGE 1: Build ----
FROM node:20 AS build

# Arbeitsverzeichnis im Container
WORKDIR /app

# package.json + lockfile zuerst kopieren (bessere Cache-Nutzung)
COPY package*.json ./

# Dependencies installieren (inkl. devDependencies wie Tailwind)
RUN npm install

# Tailwind CSS installieren, falls noch nicht vorhanden
RUN npm install -D tailwindcss postcss autoprefixer

# Falls tailwind.config.js oder postcss.config.js fehlen, automatisch erzeugen
# RUN npx tailwindcss init -p

# Rest des Frontend-Codes kopieren
COPY . .

# Optional: sicherstellen, dass das Tailwind-Setup eingebunden ist
# (Wenn du src/style.css hast, sollte es dort die @tailwind-Direktiven enthalten)
# RUN echo '@tailwind base;\n@tailwind components;\n@tailwind utilities;' > src/style.css

# Build deiner Vue-App (Vite)
RUN npm run build


# ---- STAGE 2: Serve ----
#FROM node:20-slim AS production

#WORKDIR /app

# Nur die gebauten Dateien kopieren
#COPY --from=build /app/dist ./dist

# Ein einfaches HTTP-Server-Tool (z. B. serve) installieren
#RUN npm install -g serve

# Standard-Port
#EXPOSE 5173

# Startbefehl: statische Dateien bereitstellen
#CMD ["serve", "-s", "dist", "-l", "5173"]


# Production Stage (serve mit nginx)
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80