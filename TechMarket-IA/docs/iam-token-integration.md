# Guia de uso: token IAM contra endpoints de IA

Esta guia describe como consumir endpoints de `TechMarket-IA` usando el JWT emitido por
`TechMarket-IAM`.

## Requisitos

- `TechMarket-IAM` corriendo en `http://localhost:8080`.
- `TechMarket-IA` corriendo en `http://localhost:8092`.
- Ambos servicios deben usar el mismo secreto JWT.
- IA debe confiar en el issuer de IAM: `iam-service`.

En Docker Compose, IA queda configurado con:

```yaml
IAM_JWT_ISSUER: iam-service
IAM_JWT_SECRET: replace-with-strong-32-byte-secret-value
JWT_TRUSTED_ISSUERS: iam-service,TECHMARKET-ia
```

El valor de `IAM_JWT_SECRET` debe ser igual al usado por IAM.

## 1. Levantar IAM

Desde `TechMarket-IAM/`:

```bash
docker compose up --build
```

IAM queda disponible en:

```text
http://localhost:8080
```

## 2. Levantar IA

Desde `TechMarket-IA/`:

```bash
docker compose up --build
```

IA queda disponible en:

```text
http://localhost:8092
```

## 3. Iniciar sesion en IAM

Ejemplo:

```bash
curl -X POST "http://localhost:8080/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "usuario@techmarket.com",
    "password": "password"
  }'
```

La respuesta debe incluir un JWT, normalmente en `accessToken`.

Guarda ese token:

```bash
TOKEN_IAM="<accessToken_devuelto_por_IAM>"
```

## 4. Consumir endpoints de IA

IA acepta el JWT emitido por IAM en `Authorization: Bearer`. Si el token incluye un `sub` UUID que
existe en IA, o si incluye `email`/`username` que coincide con `users.email` en IA, los endpoints
pueden resolver el usuario autenticado sin `X-User-Id`.

Mientras un endpoint o flujo no pueda resolverse por esos datos, envia tambien `X-User-Id`.

```bash
USER_ID="<uuid_del_usuario_en_IA>"
```

Ejemplos:

```bash
curl -X GET "http://localhost:8092/api/clients/profile" \
  -H "Authorization: Bearer $TOKEN_IAM" \
  -H "X-User-Id: $USER_ID"
```

```bash
curl -X GET "http://localhost:8092/api/clients/addresses" \
  -H "Authorization: Bearer $TOKEN_IAM" \
  -H "X-User-Id: $USER_ID"
```

```bash
curl -X GET "http://localhost:8092/api/clients/chats" \
  -H "Authorization: Bearer $TOKEN_IAM" \
  -H "X-User-Id: $USER_ID"
```

```bash
curl -X GET "http://localhost:8092/api/clients/notifications" \
  -H "Authorization: Bearer $TOKEN_IAM" \
  -H "X-User-Id: $USER_ID"
```

Ejemplo de endpoint de embajador resolviendo el usuario desde el token IAM:

```bash
curl -X GET "http://localhost:8092/api/ambassadors/profile" \
  -H "Authorization: Bearer $TOKEN_IAM"
```

Si el usuario local no puede resolverse desde el token, usa el header de compatibilidad:

```bash
curl -X GET "http://localhost:8092/api/ambassadors/profile" \
  -H "Authorization: Bearer $TOKEN_IAM" \
  -H "X-User-Id: $USER_ID"
```

## Comportamiento esperado

- Token IAM valido y usuario resoluble por `sub` UUID, `email` o `username`: `200 OK`.
- Token IAM valido + `X-User-Id` correcto: `200 OK`.
- Token ausente, expirado, mal firmado o con issuer no confiable: `403 Forbidden`.
- `X-User-Id` distinto al usuario del token cuando IA puede resolverlo por `email`/`username`: `403 Forbidden`.
- `X-User-Id` ausente en endpoints que todavia lo requieren y usuario no resoluble desde el token: `401 Unauthorized`.

## Como IA valida el token

IA valida:

- Firma HS256 con `app.jwt.secret`.
- Issuer incluido en `app.jwt.trusted-issuers`.
- `token_type=access`, cuando el claim existe.
- Roles desde `role` o `roles`.

Para integracion con IAM, `app.jwt.secret` puede tomar el valor de `IAM_JWT_SECRET`:

```yaml
app:
  jwt:
    secret: ${JWT_SECRET:${IAM_JWT_SECRET:TechMarket2024SecretKeyForJWTMustBe256BitsMinimumLength!!}}
    issuer: ${JWT_ISSUER:TECHMARKET-ia}
    trusted-issuers: ${JWT_TRUSTED_ISSUERS:${IAM_JWT_ISSUER:TECHMARKET-ia}}
```

## Nota sobre `X-User-Id`

IAM actualmente puede emitir `sub` como ID numerico. IA usa UUID en sus tablas locales. Por eso,
varios endpoints de IA todavia aceptan `X-User-Id` como compatibilidad.

La integracion actual intenta resolver el usuario local en este orden:

1. `sub` si es UUID y existe en `users`.
2. `email` si coincide con `users.email`.
3. `username` si coincide con `users.email`.
4. `X-User-Id` cuando el token no alcanza para resolver el UUID local.

Si se envia `X-User-Id`, IA valida que ese header no contradiga el JWT cuando puede resolver el
usuario por `sub`, `email` o `username`.

La mejora ideal a futuro es que IAM emita tambien el UUID compartido de usuario, por ejemplo:

```json
{
  "sub": "77",
  "user_uuid": "00000000-0000-0000-0000-000000000000",
  "username": "usuario@techmarket.com",
  "iss": "iam-service"
}
```

Con ese claim, IA podria dejar de depender de `X-User-Id` en todos los endpoints.

## Verificacion automatizada

La prueba agregada cubre el flujo token IAM -> endpoint IA:

```bash
cd TechMarket-IA
mvn test -Dtest=IamTokenClientEndpointIntegrationTest -Dspotless.check.skip=true
```

La prueba valida:

- Token estilo IAM aceptado para los cuatro endpoints de cliente.
- `X-User-Id` manipulado rechazado con `403`.
