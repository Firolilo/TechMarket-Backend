# TECHMARKET-ia

Vertical backend de negocio construido con Spring Boot para TechMarket.

Este repositorio contiene la base tecnica del vertical `tech-networks/feed-management`
del dominio de red social centralizada. El objetivo del proyecto es alojar la logica de negocio, APIs,
integraciones y configuracion tecnica del vertical de gestión de contenido y conexiones tecnológicas sin mezclarla con otros modulos.

## Tecnologias base

- Java 21
- Maven Wrapper
- Spring Boot 3
- Spring Web
- Spring Security
- Spring Data JPA
- Flyway
- PostgreSQL
- H2 para soporte local y pruebas
- Springdoc OpenAPI

## Requisitos

- JDK 21
- Maven 3.8.1 o superior
- Docker y Docker Compose si se ejecuta con contenedores

## Estructura general

La organizacion funcional completa del vertical se documenta en
[docs/vertical-structure.md](/C:/Users/PC/Desktop/TECHMARKET-IA/TECHMARKET-ia/docs/vertical-structure.md:1).

## Ejecutar en local

Desde la raiz del vertical:

```powershell
.\mvnw.cmd spring-boot:run
```

Opcionalmente se puede usar el script:

```powershell
.\scripts\dev-run.ps1
```

La aplicacion expone por defecto:

- Puerto HTTP: `8082`
- Health endpoint: `/actuator/health`
- Swagger UI: `/swagger-ui.html`
- OpenAPI: `/v3/api-docs`

## Ejecutar con Docker Compose

Desde la raiz del vertical:

```powershell
docker compose up --build
```

Esto levanta:

- `TECHMARKET-ia-postgres`
- `TECHMARKET-ia`

## Ejecutar pruebas

```powershell
.\mvnw.cmd test
```

O con el script:

```powershell
.\scripts\test.ps1
```

## Empaquetar el proyecto

```powershell
.\mvnw.cmd clean package
```

## Notas

- El proyecto depende de `core-platform`, por lo que el entorno de build debe tener acceso
  al workspace completo o al artefacto ya instalado localmente.
- La estructura de carpetas del vertical de negocio se mantiene separada del README operativo
  para no mezclar documentacion de arranque con documentacion arquitectonica.
