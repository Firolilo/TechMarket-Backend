# Lucas Cambios Entpoints

Fecha: 2026-05-06

## Resumen general

Se implemento el bloque de endpoints 126 al 170 del contrato `endpoint.md`, correspondiente al modulo de especialistas en `TechMarket-IA`.

La implementacion quedo bajo rutas `/api/specialists/**`, con persistencia real en base de datos, controllers dentro de paquetes `api.admin` para cumplir la prueba de arquitectura de IA, y uso del header `X-User-Id` como contexto autenticado local mientras no esta integrada la seguridad de IAM.

No se implementaron endpoints 171 en adelante. Tampoco se toco el rango Ambassador 56-125.

## Endpoints implementados

### Perfil del especialista

- `126. GET /api/specialists/profile`
  - Devuelve el perfil del tecnico autenticado.
  - Usa datos base de `users` y datos extendidos de `specialist_profiles`.
  - Responde con `id`, `nombre`, `especialidad`, `ubicacion` y `calificacion`.
  - El `id` se formatea como `TEC-{uuid}`.

- `127. PUT /api/specialists/profile`
  - Actualiza campos opcionales del perfil.
  - Acepta `nombre`, `apellido`, `especialidad` y `ubicacion`.
  - Actualiza `users.first_name`, `users.last_name` y `specialist_profiles`.
  - Responde `{ "mensaje": "Perfil actualizado correctamente" }`.

- `128. GET /api/specialists/profile/stats`
  - Calcula KPIs del tecnico autenticado.
  - `trabajosCompletados` se obtiene desde `service_appointments.assigned_technician_user_id`.
  - `totalResenas` y `calificacionPromedio` se obtienen desde `reviews.ticket_id`, enlazado con las citas del tecnico.
  - Si no hay datos, devuelve ceros.

- `129. POST /api/specialists/profile/photo`
  - Recibe JSON con `{ "url": "..." }`.
  - Persiste la URL en `specialist_profiles.photo_url`.
  - Responde `{ "url": "..." }`.
  - No se implemento storage/CDN ni multipart, por decision del plan.

### Servicios del especialista

- `130. GET /api/specialists/services`
  - Lista servicios creados por el tecnico autenticado.
  - Filtra siempre por `user_id`.
  - Responde con `id`, `nombre`, `descripcion`, `precio`, `tipo` y `destacado`.
  - El `id` se formatea como `SERV-{uuid}`.

- `131. POST /api/specialists/services`
  - Crea un nuevo servicio del tecnico autenticado.
  - Acepta `nombre`, `descripcion`, `precio`, `moneda`, `tipo` y `destacado`.
  - Si `moneda` no viene, usa `Bs`.
  - Responde con el id prefijado, nombre y mensaje: `Servicio creado exitosamente`.

- `132. PUT /api/specialists/services/{serviceId}`
  - Actualiza un servicio existente.
  - Acepta `SERV-{uuid}` o UUID crudo en path.
  - Solo permite actualizar si el servicio pertenece al usuario autenticado.
  - Responde `{ "mensaje": "Servicio actualizado" }`.

- `133. DELETE /api/specialists/services/{serviceId}`
  - Elimina un servicio del catalogo del tecnico.
  - Acepta `SERV-{uuid}` o UUID crudo en path.
  - Solo elimina servicios propios.
  - Responde `{ "mensaje": "Servicio eliminado" }`.

- `134. PATCH /api/specialists/services/{serviceId}/toggle-featured`
  - Cambia `featured` de `true` a `false` o de `false` a `true`.
  - Solo opera sobre servicios propios.
  - Responde `{ "destacado": true/false }`.

### Portafolio del especialista

- `135. GET /api/specialists/portfolio`
  - Lista trabajos del portafolio del tecnico autenticado.
  - Filtra por `user_id`.
  - Responde con `id`, `titulo`, `servicio`, `resultado` y `fecha`.
  - El `id` se formatea como `PORT-{uuid}`.

- `136. POST /api/specialists/portfolio`
  - Agrega un trabajo al portafolio.
  - Acepta `titulo`, `servicio`, `resultado` y `fecha`.
  - Responde con id prefijado y mensaje: `Trabajo agregado al portafolio`.

- `137. DELETE /api/specialists/portfolio/{itemId}`
  - Elimina un item de portafolio.
  - Acepta `PORT-{uuid}` o UUID crudo.
  - Solo elimina items propios.
  - Responde `{ "mensaje": "Trabajo eliminado del portafolio" }`.

### Disponibilidad del especialista

- `138. GET /api/specialists/availability`
  - Devuelve configuracion de disponibilidad.
  - Si no existe registro persistido, devuelve una configuracion por defecto:
    - `estado`: `disponible`
    - `dias`: lunes a sabado
    - `horario`: 08:00 a 18:00
    - `modalidad`: presencial, remoto, domicilio
    - `cobertura`: Santa Cruz de la Sierra

- `139. PUT /api/specialists/availability`
  - Actualiza configuracion de disponibilidad.
  - Acepta `estado`, `dias`, `inicio`, `fin`, `modalidad` y `cobertura`.
  - Valida `estado` contra `disponible`, `ocupado` y `ausente`.
  - Persiste `dias` y `modalidad` como JSON textual.
  - Responde `{ "mensaje": "Disponibilidad actualizada" }`.

- `140. PATCH /api/specialists/availability/status`
  - Cambia el estado actual del tecnico.
  - Acepta `estado` y `tiempoRespuesta`.
  - Valida `estado` contra `disponible`, `ocupado` y `ausente`.
  - Responde `{ "estado": "...", "tiempoRespuesta": "..." }`.

### Agenda del especialista

- `141. GET /api/specialists/calendar`
  - Devuelve citas asignadas al tecnico y bloques manuales de agenda.
  - Las citas se leen desde `service_appointments` enlazadas con `tickets` y `users`.
  - Los bloques manuales se leen desde `specialist_calendar_blocks`.

- `142. POST /api/specialists/calendar/blocks`
  - Crea un bloque no disponible para el tecnico autenticado.
  - Acepta `fecha`, `hora`, `fin` y `motivo`.
  - Responde `{ "mensaje": "Bloque agregado a la agenda" }`.

- `143. DELETE /api/specialists/calendar/blocks/{blockId}`
  - Elimina un bloque manual propio.
  - Acepta `BLK-{uuid}` o UUID crudo.
  - Responde `{ "mensaje": "Bloque eliminado" }`.

### Solicitudes y proyectos del especialista

- `144. GET /api/specialists/requests`
  - Lista solicitudes pendientes asignadas al tecnico autenticado.
  - Lee `service_appointments` con estados pendientes.
  - Responde con `REQ-{uuid}`, cliente, servicio, fecha, estado y urgencia.

- `145. PATCH /api/specialists/requests/{requestId}/respond`
  - Acepta o rechaza una solicitud propia.
  - Acepta acciones `aceptar`/`aceptada` o `rechazar`/`rechazada`.
  - Actualiza `service_appointments.status`.

- `146. GET /api/specialists/projects`
  - Lista proyectos activos del tecnico.
  - Lee citas con estados aceptados, confirmados o en progreso.

- `147. GET /api/specialists/projects/{projectId}`
  - Devuelve detalle de un proyecto propio.
  - Incluye cliente, telefono, servicio, fecha, estado y descripcion.

- `148. PATCH /api/specialists/projects/{projectId}/status`
  - Cambia estado de proyecto a `en_progreso`, `completado` o `cancelado`.
  - Actualiza `service_appointments.status`.

- `149. GET /api/specialists/projects/history`
  - Lista proyectos finalizados o cancelados.
  - Reutiliza `service_appointments` y devuelve IDs `PROJ-{uuid}`.

### Chats del especialista

- `150. GET /api/specialists/chats`
  - Lista conversaciones activas asignadas al tecnico.
  - Reutiliza `tickets`, `ticket_messages` y `chat_read_receipts`.
  - Filtra por `tickets.assigned_technician_user_id` y `ticket_type = CHAT`.

- `151. GET /api/specialists/chats/{chatId}`
  - Obtiene mensajes de una conversacion asignada al tecnico.
  - Acepta `CHT-{uuid}` o UUID crudo.
  - Lee desde `ticket_messages`.

- `152. POST /api/specialists/chats/{chatId}/messages`
  - Envia mensaje como tecnico en una conversacion asignada.
  - Persiste en `ticket_messages`.
  - Responde con `MSG-{uuid}` y fecha.

- `153. GET /api/specialists/chats/{chatId}/files`
  - Lista archivos compartidos en una conversacion.
  - Reutiliza `ticket_attachments`.

- `154. POST /api/specialists/chats/{chatId}/files`
  - Registra un archivo compartido en la conversacion.
  - Acepta `url`, `nombre`, `tipo` y `tamano`.
  - Responde con `FILE-{uuid}` y URL.

### Archivos personales del especialista

- `155. GET /api/specialists/files`
  - Lista archivos propios del tecnico autenticado.
  - Lee desde `specialist_files`.

- `156. POST /api/specialists/files`
  - Registra un archivo en el repositorio personal del tecnico.
  - Acepta `url`, `nombre`, `tipo` y `tamano`.
  - Responde con `FILE-{uuid}` y mensaje de exito.

- `157. DELETE /api/specialists/files/{fileId}`
  - Elimina un archivo propio.
  - Acepta `FILE-{uuid}` o UUID crudo.
  - Responde `{ "mensaje": "Archivo eliminado" }`.

### Wallet y transacciones del especialista

- `158. GET /api/specialists/wallet`
  - Devuelve saldo disponible, ingresos totales y monto en proceso.
  - Calcula totales desde `specialist_transactions`.

- `159. GET /api/specialists/transactions`
  - Lista historial de transacciones del tecnico.
  - Filtra por `user_id` y ordena por fecha descendente.

- `160. GET /api/specialists/transactions/{transactionId}`
  - Devuelve detalle de una transaccion propia.
  - Incluye monto, comision de plataforma y neto.

- `161. POST /api/specialists/wallet/withdraw`
  - Solicita retiro de saldo disponible.
  - Persiste la solicitud en `specialist_withdrawals`.
  - Responde monto, mensaje y fecha estimada.

- `162. GET /api/specialists/earnings/summary`
  - Devuelve resumen mensual de ingresos.
  - Calcula total, servicios realizados y promedio desde `specialist_transactions`.

### Resenas y certificaciones del especialista

- `163. GET /api/specialists/reviews`
  - Lista resenas recibidas por el tecnico.
  - Lee `reviews` enlazado con `service_appointments`, `tickets` y `users`.

- `164. GET /api/specialists/reviews/{reviewId}`
  - Devuelve detalle de una resena propia.
  - Incluye respuesta del tecnico si existe.

- `165. POST /api/specialists/reviews/{reviewId}/respond`
  - Responde una resena recibida.
  - Persiste `reviews.technician_response` y `technician_response_at`.

- `166. GET /api/specialists/certifications`
  - Lista certificaciones del tecnico.
  - Lee desde `specialist_certifications`.

- `167. POST /api/specialists/certifications`
  - Agrega una certificacion.
  - Acepta `nombre`, `institucion`, `fechaObtencion` y `archivoUrl`.

- `168. DELETE /api/specialists/certifications/{certId}`
  - Elimina una certificacion propia.
  - Acepta `CERT-{uuid}` o UUID crudo.

- `169. PATCH /api/specialists/certifications/{certId}/verify`
  - Cambia la certificacion a `en_verificacion`.
  - Responde mensaje de envio para verificacion.

### Asistente IA del especialista

- `170. POST /api/specialists/ai/query`
  - Devuelve una respuesta deterministica de asistencia operativa.
  - Persiste historial simple en `specialist_ai_queries`.
  - No llama a un proveedor externo de IA por ahora.

## Persistencia agregada

Se agregaron las migraciones:

- `TechMarket-IA/src/main/resources/db/migration/V77__specialist_profile_services_portfolio_availability.sql`
- `TechMarket-IA/src/main/resources/db/migration/V78__specialist_calendar_blocks.sql`
- `TechMarket-IA/src/main/resources/db/migration/V79__specialist_files_and_transactions.sql`
- `TechMarket-IA/src/main/resources/db/migration/V80__specialist_withdrawals_certifications_ai_reviews.sql`

Tablas nuevas:

- `specialist_profiles`
  - Perfil extendido por tecnico.
  - Campos principales: `id`, `user_id`, `specialty`, `location`, `photo_url`, `created_at`, `updated_at`.
  - Tiene constraint unico por `user_id`.

- `specialist_services`
  - Servicios publicados por tecnico.
  - Campos principales: `id`, `user_id`, `name`, `description`, `price`, `currency`, `service_type`, `featured`, timestamps.

- `specialist_portfolio_items`
  - Trabajos del portafolio.
  - Campos principales: `id`, `user_id`, `title`, `service_name`, `result`, `work_date`, timestamps.

- `specialist_availability`
  - Configuracion de disponibilidad por tecnico.
  - Campos principales: `id`, `user_id`, `status`, `days_json`, `start_time`, `end_time`, `modalities_json`, `coverage`, `response_time`, timestamps.
  - Tiene constraint unico por `user_id`.

- `specialist_calendar_blocks`
  - Bloques manuales de agenda no disponible por tecnico.
  - Campos principales: `id`, `user_id`, `block_date`, `start_time`, `end_time`, `reason`, timestamps.

- `specialist_files`
  - Repositorio personal de archivos del tecnico.
  - Campos principales: `id`, `user_id`, `file_url`, `file_name`, `file_type`, `file_size`, timestamps.

- `specialist_transactions`
  - Historial de ingresos del tecnico.
  - Campos principales: `id`, `user_id`, `service_name`, `client_name`, `amount`, `platform_commission`, `currency`, `status`, `transaction_date`, timestamps.

Tambien se agrego `ticket_attachments.file_size` para responder el tamano de archivos compartidos en chat.

- `specialist_withdrawals`
  - Solicitudes de retiro del tecnico.
  - Campos principales: `id`, `user_id`, `amount`, `currency`, `status`, `requested_at`, `estimated_at`.

- `specialist_certifications`
  - Certificaciones del tecnico.
  - Campos principales: `id`, `user_id`, `name`, `institution`, `obtained_at`, `file_url`, `status`, timestamps.

- `specialist_ai_queries`
  - Historial simple de consultas al asistente IA.
  - Campos principales: `id`, `user_id`, `query_text`, `focus`, `response_summary`, `created_at`.

Tambien se agregaron `reviews.technician_response` y `reviews.technician_response_at`.

Indices agregados:

- `idx_specialist_services_user_id`
- `idx_specialist_portfolio_items_user_id`
- `idx_service_appointments_technician_status`
- `idx_reviews_ticket_id`
- `idx_specialist_calendar_blocks_user_date`
- `idx_specialist_files_user_id`
- `idx_specialist_transactions_user_date`
- `idx_specialist_withdrawals_user_id`
- `idx_specialist_certifications_user_id`
- `idx_specialist_ai_queries_user_id`

## Codigo agregado

### Helpers de aplicacion

- `SpecialistIdentitySupport`
  - Valida `X-User-Id`.
  - Devuelve `401` si falta el header.
  - Devuelve `400` si el UUID es invalido.
  - Devuelve `404` si el usuario no existe.
  - Formatea IDs `TEC-`, `SERV-`, `PORT-`, `BLK-`, `REQ-`, `PROJ-`, `CHT-`, `MSG-`, `FILE-`, `TX-` y `CERT-`.
  - Permite parsear IDs prefijados o UUID crudos.

- `SpecialistJsonListMapper`
  - Serializa listas de strings a JSON textual.
  - Deserializa JSON textual a listas.
  - Se usa para `dias` y `modalidad` en disponibilidad.

### Entidades JPA

- `SpecialistProfileJpaEntity`
- `SpecialistServiceJpaEntity`
- `SpecialistPortfolioItemJpaEntity`
- `SpecialistAvailabilityJpaEntity`
- `SpecialistCalendarBlockJpaEntity`
- `SpecialistFileJpaEntity`
- `SpecialistTransactionJpaEntity`
- `SpecialistWithdrawalJpaEntity`
- `SpecialistCertificationJpaEntity`
- `SpecialistAiQueryJpaEntity`
- `SpecialistServiceAppointmentJpaEntity`
- `SpecialistReviewJpaEntity`
  - `ClientChatAttachmentJpaEntity` reutiliza `ticket_attachments` para archivos de chat.

Las dos ultimas entidades se usan para lectura de KPIs sobre tablas existentes.

### Repositorios

- `SpecialistProfileSpringDataRepository`
- `SpecialistServiceSpringDataRepository`
- `SpecialistPortfolioItemSpringDataRepository`
- `SpecialistAvailabilitySpringDataRepository`
- `SpecialistCalendarBlockSpringDataRepository`
- `SpecialistFileSpringDataRepository`
- `SpecialistTransactionSpringDataRepository`
- `SpecialistWithdrawalSpringDataRepository`
- `SpecialistCertificationSpringDataRepository`
- `SpecialistAiQuerySpringDataRepository`
- `SpecialistReviewSpringDataRepository`
- `SpecialistReviewProjection`
- `SpecialistServiceAppointmentSpringDataRepository`
- `SpecialistAppointmentSummaryProjection`
- `SpecialistReviewStatsSpringDataRepository`
- `SpecialistReviewStatsProjection`
  - `ClientChatAttachmentSpringDataRepository` reutiliza adjuntos de tickets.

### Controllers

- `SpecialistProfileController`
  - Maneja perfil, stats y foto.

- `SpecialistServiceController`
  - Maneja listado, creacion, actualizacion, borrado y destacado de servicios.

- `SpecialistPortfolioController`
  - Maneja listado, creacion y borrado de portafolio.

- `SpecialistAvailabilityController`
  - Maneja consulta, actualizacion y cambio de estado de disponibilidad.

- `SpecialistCalendarController`
  - Maneja agenda y bloques manuales.

- `SpecialistRequestController`
  - Maneja listado y respuesta de solicitudes.

- `SpecialistProjectController`
  - Maneja proyectos activos, detalle, estado e historial.

- `SpecialistChatController`
  - Maneja listado de chats activos, mensajes y archivos compartidos.

- `SpecialistFileController`
  - Maneja archivos personales del tecnico.

- `SpecialistWalletController`
  - Maneja wallet, retiros, resumen de ingresos y transacciones del tecnico.

- `SpecialistReviewController`
  - Maneja resenas recibidas y respuestas del tecnico.

- `SpecialistCertificationController`
  - Maneja certificaciones y solicitud de verificacion.

- `SpecialistAiController`
  - Maneja consulta operativa deterministica del asistente IA.

Todos los controllers quedaron bajo:

- `com.techmarket.techmarket.specialists.api.admin`

Esto se hizo para cumplir la regla de arquitectura existente:

- Todo `*Controller` en `TechMarket-IA` debe residir en paquetes `..api.admin..`.

## Contratos y validaciones

### Autenticacion local

Todos los endpoints usan:

- Header requerido: `X-User-Id`

Errores:

- Header ausente: `401 Unauthorized`
- UUID invalido: `400 Bad Request`
- Usuario inexistente: `404 Not Found`

### Ownership

Los recursos de servicios y portafolio siempre se consultan por:

- `id`
- `user_id`

Esto evita que un tecnico modifique o elimine recursos de otro usuario.

### Estados validos de disponibilidad

Los estados aceptados son:

- `disponible`
- `ocupado`
- `ausente`

Cualquier otro valor devuelve `400 Bad Request`.

## Pruebas agregadas

Se agrego:

- `TechMarket-IA/src/test/java/com/TechMarket/techmarket/specialists/api/admin/SpecialistControllerTest.java`

Casos cubiertos:

- `GET /api/specialists/profile` sin header devuelve `401`.
- `GET /api/specialists/profile` con UUID invalido devuelve `400`.
- `GET /api/specialists/profile/stats` devuelve trabajos completados, total de resenas y promedio redondeado.
- `POST /api/specialists/profile/photo` persiste y devuelve URL.
- `POST /api/specialists/services` crea servicio y devuelve `SERV-{uuid}`.
- `PUT /api/specialists/services/{serviceId}` actualiza servicio propio.
- `PUT /api/specialists/services/{serviceId}` devuelve `404` si el servicio no pertenece al usuario.
- `DELETE /api/specialists/services/{serviceId}` elimina servicio propio.
- `DELETE /api/specialists/portfolio/{itemId}` elimina item propio.
- `PUT /api/specialists/availability` persiste configuracion.
- `PATCH /api/specialists/availability/status` rechaza estado invalido.
- `GET /api/specialists/calendar` devuelve citas y bloques manuales.
- `POST /api/specialists/calendar/blocks` persiste bloque propio.
- `PATCH /api/specialists/requests/{requestId}/respond` acepta solicitud propia.
- `PATCH /api/specialists/projects/{projectId}/status` cambia estado permitido.
- `GET /api/specialists/chats` lista chats asignados con conteo de no leidos.
- `GET /api/specialists/chats/{chatId}` lista mensajes de chat asignado.
- `POST /api/specialists/files` persiste archivo propio.
- `GET /api/specialists/wallet` devuelve montos agregados.
- `POST /api/specialists/wallet/withdraw` persiste solicitud de retiro.
- `POST /api/specialists/ai/query` devuelve plan de accion y persiste consulta.

## Documentacion actualizada

Se actualizo `AGENTS.md` para dejar registrado:

- Endpoints 126-140 implementados en `TechMarket-IA`.
- Uso de `X-User-Id` para contexto local.
- Nueva migracion `V77__specialist_profile_services_portfolio_availability.sql`.
- Nuevas tablas `specialist_*`.
- Nuevos prefijos:
  - `TEC-{uuid}`
  - `SERV-{uuid}`
  - `PORT-{uuid}`

## Verificacion realizada

Se ejecuto:

```powershell
git diff --check
```

Resultado:

- Sin errores de whitespace en los archivos nuevos.
- Git mostro solo un aviso de line endings para `AGENTS.md`: `LF will be replaced by CRLF the next time Git touches it`.

Se intento ejecutar:

```powershell
.\mvnw.cmd -Dspotless.check.skip=true test
```

desde `TechMarket-IA`, pero no fue posible porque el entorno actual no tiene `JAVA_HOME` configurado:

```text
Error: JAVA_HOME not found in your environment.
Please set the JAVA_HOME variable in your environment to match the
location of your Java installation.
```

Tambien se verifico que `java`, `mvn` y `JAVA_HOME` no estaban disponibles en la sesion actual.

## Estado final

El bloque 126-140 quedo implementado con persistencia real, validaciones, ownership por usuario, tests MVC y documentacion del contexto del repo.

Pendiente externo al codigo:

- Configurar JDK 21 y `JAVA_HOME` para ejecutar la suite Maven completa de `TechMarket-IA`.
