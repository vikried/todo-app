# ---- STAGE 1: Build ----
FROM node:20 AS build

WORKDIR /app

COPY package*.json ./
RUN npm install
RUN npm install -D tailwindcss postcss autoprefixer

COPY . .
RUN npm run build

# ---- STAGE 2: Serve ----
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
