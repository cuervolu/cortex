# Proyecto Cortex

Cortex es un proyecto monorepo que contiene un backend en Java Spring Boot, un frontend en Nuxt.js, y un paquete compartido para componentes y utilidades comunes.

## Requisitos previos

Antes de comenzar, asegúrate de tener instalado lo siguiente:

1. Java 21 (OpenJDK)
2. Docker y Docker Compose
3. IDE de tu preferencia (recomendado: IntelliJ IDEA y WebStorm o VS Code)
4. Maven
5. pnpm

## Configuración inicial

1. Clona el repositorio:
   ```bash
   git clone https://github.com/tu-usuario/cortex.git
   cd cortex
   ```

2. Copia el archivo `.env.template` y renómbralo a `.env`:
   ```bash
   cp .env.template .env
   ```

3. Edita el archivo `.env` y añade las siguientes variables con tus propios valores:
   ```
   # PostgreSQL Config
   POSTGRES_DB=cortex_db
   POSTGRES_USER=your_user
   POSTGRES_PASSWORD=your_password

   # Directus Config
   SECRET=your_secret
   ADMIN_EMAIL=admin@your_domain.com
   ADMIN_PASSWORD=your_password
   DB_CLIENT=pg
   DB_HOST=postgres
   DB_PORT=5432
   DB_DATABASE=cortex_db
   DB_USER=your_user
   DB_PASSWORD=your_password
   WEBSOCKETS_ENABLED=true
   ```

4. Descarga las imágenes de Docker necesarias:
   ```bash
   docker pull python:3.12-slim
   docker pull eclipse-temurin:21
   docker pull node:20-alpine3.19
   docker pull rust:1.80-slim
   docker pull mcr.microsoft.com/dotnet/sdk:8.0
   docker pull golang:1.22-bookworm
   ```

## Instalación de dependencias

1. Instala las dependencias del proyecto:
   ```bash
   pnpm install
   ```

## Ejecución del proyecto

1. Inicia los servicios de Docker:
   ```bash
   docker-compose up -d
   ```

2. Inicia el backend (desde el directorio raíz):
   ```bash
   cd packages/backend
   ./mvnw spring-boot:run
   ```

3. Inicia el frontend (desde el directorio raíz):
   ```bash
   pnpm dev:frontend
   ```
