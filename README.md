# Cortex

<p align="center">
  <img src=".github/images/icon1_dark (512px).png" alt="Logo de Cortex" width="200" height="200">
</p>

<p align="center">
  <img src="https://img.shields.io/badge/estado-en%20desarrollo-yellow" alt="En Desarrollo">
</p>

Cortex es una plataforma educativa de código abierto diseñada para facilitar el aprendizaje de tecnologías de la información y programación, enfocada principalmente en jóvenes y niños hispanohablantes. El proyecto busca democratizar el acceso a la educación tecnológica, ofreciendo una experiencia de aprendizaje interactiva, accesible y actualizada.

Este proyecto se desarrolla como parte de la asignatura de Capstone en Duoc UC Puente Alto, sección 002D.

## Estructura del Proyecto

Este proyecto es un monorepo que propone desarrollar un Producto Mínimo Viable (MVP) de Cortex, compuesto por tres elementos principales:

1. **Backend API Monolítico:** Un sistema robusto implementado con Spring Boot para manejar la lógica de negocio, autenticación, gestión de contenidos y ejecución segura de código.

2. **Aplicación Web:** Una interfaz intuitiva y responsive desarrollada con Nuxt.js, que ofrece lecciones interactivas, ejercicios prácticos y seguimiento del progreso del estudiante.

3. **Aplicación de Escritorio:** Una aplicación nativa para usuarios premium, desarrollada con Tauri (Versión 2), que proporciona funcionalidades avanzadas y acceso offline.

Esta estructura permite una experiencia de aprendizaje completa y escalable, implementando un modelo freemium que garantiza la accesibilidad y sostenibilidad del proyecto.

Utilizamos Docker para ejecutar los servicios, incluyendo:

- Una base de datos PostgreSQL para almacenamiento persistente
- [Redis](https://redis.io/) para almacenamiento en caché
- [RabbitMQ](https://www.rabbitmq.com/) para la gestión de colas de mensajes
- [Bruno](https://www.usebruno.com/) como REST Client para pruebas de API
- [Resend](https://resend.com/) para el envío de emails
- [MercadoPago](https://www.mercadopago.cl/developers/es) para el procesamiento de pagos
- [Cloudinary](https://cloudinary.com/) para el almacenamiento de imágenes

## Requisitos previos

Antes de comenzar, asegúrate de tener instalado lo siguiente:

1. Java 22 (OpenJDK)
2. Docker Desktop (Windows y macOS) o Docker Engine (Linux)
3. IDE de tu preferencia (recomendado: IntelliJ IDEA y WebStorm o VS Code)
4. Maven
5. pnpm
6. Git

## Configuración del entorno

### Variables de entorno generales

Crea un archivo `.env` en la raíz del proyecto con el siguiente contenido:

```env
# Configuración de PostgreSQL
POSTGRES_DB=cortex_db
POSTGRES_USER=your_user
POSTGRES_PASSWORD=your_password

CLAUDE_API_KEY=your_api_key
```

### Variables de entorno del backend

El backend requiere su propio archivo `.env` con las siguientes variables adicionales:

```env
RESEND_API_KEY=your_resend_api_key

GITHUB_CLIENT_ID=your_github_client_id
GITHUB_CLIENT_SECRET=your_github_client_secret

GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret

MERCADOPAGO_ACCESS_TOKEN=your_mercadopago_access_token

CLOUDINARY_CLOUD_NAME=your_cloudinary_cloud_name
CLOUDINARY_API_KEY=your_cloudinary_api_key
CLOUDINARY_API_SECRET=your_cloudinary_api_secret
```

## Configuración de imágenes Docker

- En Windows y macOS, puedes ejecutar el script `pull_docker_images.py` para descargar las imágenes necesarias:

```bash
python pull_docker_images.py
```

- Si no tienes Python en Windows o prefieres hacerlo manualmente, puedes ejecutar los siguientes comandos:

```bash
docker pull python:3.12-slim
docker pull maven:3.9.9-eclipse-temurin-21
docker pull rust:1.80-slim
docker pull mcr.microsoft.com/dotnet/sdk:8.0
docker pull golang:1.23-bookworm
```

## Solución de problemas comunes

- Si encuentras problemas con los permisos al ejecutar scripts en Windows, puedes necesitar cambiar la política de ejecución de PowerShell temporalmente:

```powershell
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
```

Recuerda volver a establecer la política original después:

```powershell
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Default
```

- Si Docker no inicia correctamente en Windows, asegúrate de que el servicio de Docker Desktop esté en ejecución y que Windows Subsystem for Linux 2 (WSL2) esté instalado y actualizado.

## Autores

- Ángel Cuervo
- Ignacio Carrasco
- Hamir Llanos
