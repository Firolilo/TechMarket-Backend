# Guía de Desarrollo - AI Service en Fedora

Esta guía adapta el flujo de desarrollo local para Fedora usando `bash` o `zsh`.

## Requisitos Previos

- Java 21
- Maven 3.8.1 o superior
- Docker Engine con Docker Compose
- `curl`

## Instalar dependencias en Fedora

```bash
sudo dnf install -y java-21-openjdk-devel maven docker docker-compose-plugin curl
sudo systemctl enable --now docker
sudo usermod -aG docker "$USER"
newgrp docker
```

Verifica el entorno:

```bash
java -version
mvn -version
docker --version
docker compose version
```

Si Fedora tiene varias versiones de Java instaladas, fuerza Java 21 antes de compilar o arrancar:

```bash
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk
export PATH="$JAVA_HOME/bin:$PATH"

java -version
mvn -version
```

Ambos comandos deben mostrar Java `21`.

Si quieres evitar depender del estado de la shell, puedes ejecutar Maven así:

```bash
env JAVA_HOME=/usr/lib/jvm/java-21-openjdk \
  PATH=/usr/lib/jvm/java-21-openjdk/bin:$PATH \
  mvn -DskipTests clean install
```

## Paso 1: Levantar Postgres

Desde la raíz de `TechMarket-AI`:

```bash
docker compose up -d
docker compose ps
```

Esperar a que el contenedor quede saludable:

```bash
until [ "$(docker inspect -f '{{.State.Health.Status}}' ai-service-postgres 2>/dev/null)" = "healthy" ]; do
  sleep 2
done
```

La base de datos queda disponible por defecto en:

- Host: `127.0.0.1`
- Puerto: `5434`
- Base de datos: `ai_service`
- Usuario: `postgres`
- Password: `postgres`

## Paso 2: Compilar el proyecto

Este repositorio no incluye `mvnw` para Linux, solo `mvnw.cmd`, así que en Fedora debes usar Maven instalado en el sistema:

```bash
env JAVA_HOME=/usr/lib/jvm/java-21-openjdk \
  PATH=/usr/lib/jvm/java-21-openjdk/bin:$PATH \
  mvn -DskipTests clean install
```

Si solo quieres compilar los módulos necesarios para arrancar el servicio:

```bash
env JAVA_HOME=/usr/lib/jvm/java-21-openjdk \
  PATH=/usr/lib/jvm/java-21-openjdk/bin:$PATH \
  mvn -pl modules/ai-bootstrap -am install -DskipTests
```

## Paso 3: Ejecutar el servicio en perfil dev

```bash
env JAVA_HOME=/usr/lib/jvm/java-21-openjdk \
  PATH=/usr/lib/jvm/java-21-openjdk/bin:$PATH \
  mvn -f modules/ai-bootstrap/pom.xml \
  -Dspring-boot.run.profiles=dev \
  spring-boot:run
```

El puerto por defecto en el perfil `dev` actual es:

- Servicio HTTP: `8091`

Para usar otro puerto:

```bash
env JAVA_HOME=/usr/lib/jvm/java-21-openjdk \
  PATH=/usr/lib/jvm/java-21-openjdk/bin:$PATH \
  mvn -f modules/ai-bootstrap/pom.xml \
  -Dspring-boot.run.profiles=dev \
  -Dspring-boot.run.arguments=--server.port=8082 \
  spring-boot:run
```

## Verificación

```bash
curl http://localhost:8091/actuator/health
```

Swagger y OpenAPI:

```bash
curl http://localhost:8091/v3/api-docs
xdg-open http://localhost:8091/swagger-ui.html
```

## Detener servicios

Detener Spring Boot:

```bash
Ctrl+C
```

Detener Postgres:

```bash
docker compose down
```

Si también quieres borrar el volumen local:

```bash
docker compose down -v
```

## Variables útiles

Puedes sobrescribir la configuración sin editar archivos:

```bash
export AI_SERVICE_PORT=8082
export AI_DB_HOST=127.0.0.1
export AI_DB_PORT=5434
export AI_DB_NAME=ai_service
export AI_DB_USER=postgres
export AI_DB_PASSWORD=postgres
```

Luego arranca normalmente:

```bash
env JAVA_HOME=/usr/lib/jvm/java-21-openjdk \
  PATH=/usr/lib/jvm/java-21-openjdk/bin:$PATH \
  mvn -f modules/ai-bootstrap/pom.xml \
  -Dspring-boot.run.profiles=dev \
  spring-boot:run
```

## Troubleshooting

**Error: `No qualifying bean of type LlmChatPort`**

- Verifica que estés usando el perfil `dev`
- Arranca con `-Dspring-boot.run.profiles=dev`

**Error: conexión a Postgres**

- Verifica contenedores: `docker compose ps`
- Verifica healthcheck: `docker inspect -f '{{.State.Health.Status}}' ai-service-postgres`
- Verifica puerto expuesto: `5434`

**Error: `mvn: command not found`**

- Instala Maven: `sudo dnf install -y maven`

**Error al usar Docker sin sudo**

- Verifica tu grupo: `groups`
- Si `docker` no aparece, cierra sesión y vuelve a entrar

**Error de `spotless` con `NoSuchMethodError`**

- Suele pasar si ejecutas Maven con Java `25`
- Este proyecto debe construirse con Java `21`
- Verifica con `mvn -version`
- Corrige la sesión actual con:

```bash
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk
export PATH="$JAVA_HOME/bin:$PATH"
mvn -DskipTests clean install
```
