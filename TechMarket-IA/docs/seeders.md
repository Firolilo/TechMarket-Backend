# Seeders de datos iniciales

Los seeders de `TechMarket-IA` se aplican mediante Flyway. Actualmente el seeder de empresas, servicios, marketplace y comunidades vive en:

```text
src/main/resources/db/migration/V96__seed_companies_services_marketplace_communities.sql
```

## Que datos carga

La migracion `V96` inserta datos iniciales para:

- Usuarios de soporte para especialistas, autores y miembros de comunidades.
- Empresas (`tenants`), perfiles de empresa y sucursales.
- Categorias de negocio, categorias de catalogo y marcas.
- Productos y servicios del marketplace.
- Imagenes, especificaciones, inventario y detalles de servicio.
- Perfiles y servicios de especialistas.
- Comunidades, membresias y publicaciones iniciales.

Los registros usan UUIDs deterministicas y `ON CONFLICT`, por lo que el script es estable frente a conflictos por ID. Flyway, de todas formas, ejecuta cada migracion versionada una sola vez por base de datos.

## Aplicacion automatica

En una base de datos nueva, Flyway ejecuta las migraciones en orden:

```text
V1 -> V2 -> ... -> V95 -> V96
```

Cuando la aplicacion arranca con Flyway habilitado, no hay que ejecutar el seeder manualmente. La migracion queda registrada en la tabla `flyway_schema_history`.

En una base existente que ya tenga migraciones hasta `V95`, Flyway detecta que falta `V96` y la aplica una sola vez durante el siguiente arranque con Flyway habilitado.

## Forma recomendada con Docker Compose

Desde `TechMarket-IA/`:

```bash
docker compose up --build
```

El servicio usa el perfil `docker`, que tiene:

```yaml
spring:
  flyway:
    enabled: true
```

Con ese perfil, PostgreSQL se levanta en el contenedor y Flyway aplica automaticamente las migraciones pendientes, incluyendo `V96`.

## Forma recomendada sin Docker

Arranca la aplicacion contra PostgreSQL con Flyway habilitado. Por ejemplo:

```bash
SPRING_PROFILES_ACTIVE=docker \
TECHMARKET_IA_DB_URL=jdbc:postgresql://localhost:5435/TECHMARKET_ia \
TECHMARKET_IA_DB_USERNAME=TECHMARKET_ia_user \
TECHMARKET_IA_DB_PASSWORD=TECHMARKET_ia_password \
./mvnw spring-boot:run
```

Ajusta las variables de conexion si tu PostgreSQL usa otro host, puerto, base o credenciales.

## Importante sobre el perfil dev

El perfil por defecto es `dev`. Ese perfil usa H2 en memoria y tiene Flyway deshabilitado:

```yaml
spring:
  flyway:
    enabled: false
```

Por eso, si ejecutas simplemente:

```bash
./mvnw spring-boot:run
```

sin cambiar el perfil, los seeders de Flyway no se aplican. Para probar los datos seed, usa un perfil con Flyway habilitado, como `docker`, o habilita Flyway explicitamente en un perfil local contra PostgreSQL.

## Como verificar que se aplico

En PostgreSQL puedes consultar:

```sql
SELECT version, description, success
FROM flyway_schema_history
WHERE version = '96';
```

Tambien puedes validar datos representativos:

```sql
SELECT business_name FROM tenants WHERE id = '50000000-0000-0000-0000-000000000101';
SELECT title FROM listings WHERE id = '60000000-0000-0000-0000-000000000101';
SELECT name FROM communities WHERE id = '80000000-0000-0000-0000-000000000101';
SELECT name FROM specialist_services WHERE id = '71000000-0000-0000-0000-000000000101';
```

## Cuando seria manual

Solo tendrias que aplicar el SQL manualmente si:

- Flyway esta deshabilitado.
- Estas cargando una base desde un dump que no ejecuta migraciones.
- Quieres insertar estos datos en una base administrada fuera del arranque de Spring Boot.

En el flujo normal del proyecto, no ejecutes el archivo manualmente: deja que Flyway lo controle para evitar desorden en el historial de migraciones.
