# Lucas Cambios Entpoints

Fecha: 2026-05-06

## Resumen general

Se implemento el bloque de endpoints 56 al 226 del contrato `endpoint.md`, correspondiente al modulo de embajadores, especialistas, busqueda general, notificaciones generales, mensajeria unificada, pagos/transacciones, facturas, soporte, reportes, configuracion publica, endpoints admin y moderacion en `TechMarket-IA`.

La implementacion quedo bajo rutas `/api/ambassadors/**`, `/api/specialists/**` y rutas generales, con persistencia real en base de datos, controllers dentro de paquetes `api.admin` para cumplir la prueba de arquitectura de IA, y uso del header `X-User-Id` como contexto autenticado local mientras no esta integrada la seguridad de IAM.

El rango Ambassador 56-125 queda completo.

## Endpoints implementados

### Embajador: perfil y configuracion

- `56. GET /api/ambassadors/profile`
  - Devuelve perfil del embajador autenticado con `AMB-{uuid}`.
  - Usa `X-User-Id`, `users` y `ambassadors`.

- `57. PUT /api/ambassadors/profile`
  - Actualiza nombre, apellido, telefono, ciudad y descripcion.

- `58. POST /api/ambassadors/profile/photo`
  - Persiste URL de avatar en `ambassadors.avatar_url`.

- `59. GET /api/ambassadors/profile/stats`
  - Calcula referidos, activos, conversion rate y comisiones desde `ambassador_referrals` y `ambassador_commissions`.

- `60. GET /api/ambassadors/settings`
  - Devuelve preferencias del embajador.

- `61. PUT /api/ambassadors/settings`
  - Actualiza notificaciones y visibilidad publica.

### Embajador: links y codigos de referido

- `62. GET /api/ambassadors/referral-links`
  - Lista links propios desde `ambassador_referral_links`.

- `63. POST /api/ambassadors/referral-links`
  - Crea link con codigo y URL de registro.

- `64. GET /api/ambassadors/referral-links/{linkId}`
  - Devuelve detalle y conversion rate.

- `65. PUT /api/ambassadors/referral-links/{linkId}`
  - Actualiza nombre y segmento.

- `66. PATCH /api/ambassadors/referral-links/{linkId}/status`
  - Activa o desactiva un link propio.

- `67. DELETE /api/ambassadors/referral-links/{linkId}`
  - Elimina link propio.

- `68. GET /api/ambassadors/referral-links/{linkId}/qr`
  - Devuelve URL deterministica de QR.

- `69. GET /api/ambassadors/referral-codes`
  - Lista codigos activos del embajador.

### Embajador: referidos, onboarding y leads

- `70. GET /api/ambassadors/referrals`
  - Lista negocios o usuarios referidos.

- `71. POST /api/ambassadors/referrals`
  - Registra prospecto referido manualmente.

- `72. GET /api/ambassadors/referrals/{referralId}`
  - Devuelve detalle de un referido propio.

- `73. PUT /api/ambassadors/referrals/{referralId}`
  - Actualiza contacto, telefono y ciudad.

- `74. PATCH /api/ambassadors/referrals/{referralId}/status`
  - Actualiza estado del referido.

- `75. DELETE /api/ambassadors/referrals/{referralId}`
  - Elimina solo referidos en estado `prospecto`.

- `76. GET /api/ambassadors/referrals/{referralId}/activity`
  - Lista actividad del referido.

- `77. POST /api/ambassadors/referrals/{referralId}/notes`
  - Agrega nota interna.

- `78. GET /api/ambassadors/referrals/{referralId}/notes`
  - Lista notas internas.

- `79. POST /api/ambassadors/referrals/{referralId}/files`
  - Registra archivo asociado al referido.

- `80. GET /api/ambassadors/onboarding`
  - Lista procesos activos de onboarding basados en referidos no activos.

- `81. GET /api/ambassadors/onboarding/{onboardingId}`
  - Devuelve detalle, progreso e hitos.

- `82. POST /api/ambassadors/onboarding/{onboardingId}/tasks`
  - Crea tarea de seguimiento.

- `83. GET /api/ambassadors/onboarding/{onboardingId}/tasks`
  - Lista tareas de onboarding.

- `84. PATCH /api/ambassadors/onboarding/tasks/{taskId}/status`
  - Actualiza estado de tarea propia.

- `85. POST /api/ambassadors/onboarding/{onboardingId}/reminders`
  - Crea recordatorio de seguimiento.

- `86. GET /api/ambassadors/onboarding/milestones`
  - Lista hitos disponibles.

- `87. PATCH /api/ambassadors/onboarding/{onboardingId}/milestones/{milestoneId}`
  - Marca hito como completado.

- `88. GET /api/ambassadors/leads`
  - Lista leads capturados.

- `89. POST /api/ambassadors/leads`
  - Crea lead capturado.

- `90. GET /api/ambassadors/leads/{leadId}`
  - Devuelve detalle de un lead propio.

- `91. PUT /api/ambassadors/leads/{leadId}`
  - Actualiza datos base del lead capturado.
  - Responde `{ "mensaje": "Lead actualizado" }`.

- `92. PATCH /api/ambassadors/leads/{leadId}/status`
  - Actualiza el estado del lead propio.
  - Responde con `LEAD-{uuid}` y el nuevo estado.

- `93. POST /api/ambassadors/leads/{leadId}/convert`
  - Convierte un lead propio en referido dentro de `ambassador_referrals`.
  - Marca el lead como `convertido`.
  - Responde con `BUS-{uuid}` y mensaje de conversion.

- `94. DELETE /api/ambassadors/leads/{leadId}`
  - Elimina un lead propio.

### Embajador: comisiones y wallet

- `95. GET /api/ambassadors/commissions`
  - Lista comisiones propias desde `ambassador_commissions`.
  - Responde con `COM-{uuid}`, referido, concepto, monto, estado y fecha.

- `96. GET /api/ambassadors/commissions/summary`
  - Calcula total generado, disponible, pendiente y pagado desde las comisiones del embajador.

- `97. GET /api/ambassadors/commissions/{commissionId}`
  - Devuelve detalle de una comision propia.
  - Acepta `COM-{uuid}` o UUID crudo.

- `98. POST /api/ambassadors/commissions/{commissionId}/dispute`
  - Registra disputa de comision en `ambassador_commission_disputes`.
  - Responde con `DSP-{uuid}` y estado `pendiente_revision`.

- `99. GET /api/ambassadors/wallet`
  - Devuelve saldo disponible, saldo pendiente y total retirado.
  - Calcula saldos desde `ambassador_commissions` y `ambassador_withdrawals`.

- `100. POST /api/ambassadors/wallet/withdraw`
  - Crea solicitud de retiro en `ambassador_withdrawals`.
  - Responde con `WDR-{uuid}`, estado `pendiente` y fecha estimada.

### Embajador: retiros, red y chat

- `101. GET /api/ambassadors/payouts`
  - Lista solicitudes de retiro del embajador desde `ambassador_withdrawals`.

- `102. GET /api/ambassadors/payout-methods`
  - Lista metodos de pago de retiro desde `ambassador_payout_methods`.

- `103. POST /api/ambassadors/payout-methods`
  - Agrega metodo de pago con `PAYM-{uuid}`.
  - Guarda banco, titular, numero de cuenta y ultimos 4 digitos.

- `104. DELETE /api/ambassadors/payout-methods/{methodId}`
  - Elimina metodo de pago propio.

- `105. GET /api/ambassadors/network`
  - Lista subembajadores directos desde `ambassadors.sponsor_ambassador_id`.

- `106. GET /api/ambassadors/network/tree`
  - Devuelve arbol de red directo del embajador autenticado.

- `107. POST /api/ambassadors/network/invitations`
  - Crea invitacion de embajador en `ambassador_invitations`.
  - Responde con `INV-AMB-{uuid}` y estado `enviada`.

- `108. GET /api/ambassadors/network/invitations`
  - Lista invitaciones enviadas por el embajador.

- `109. DELETE /api/ambassadors/network/invitations/{invitationId}`
  - Cancela invitacion propia eliminandola.

- `110. GET /api/ambassadors/network/ranking`
  - Lista ranking de embajadores ordenado por comisiones generadas.

- `111. GET /api/ambassadors/chats`
  - Lista conversaciones del embajador.
  - Reusa `tickets` con `ticket_type = 'AMBASSADOR_CHAT'`.

- `112. POST /api/ambassadors/chats`
  - Crea conversacion contra un referido `BUS-{uuid}`.
  - Registra mensaje inicial en `ticket_messages`.

- `113. GET /api/ambassadors/chats/{chatId}/messages`
  - Lista mensajes de una conversacion propia.

- `114. POST /api/ambassadors/chats/{chatId}/messages`
  - Envia mensaje del embajador.
  - Responde con `MSG-{uuid}` y estado `enviado`.

- `115. PUT /api/ambassadors/chats/{chatId}/read`
  - Marca conversacion como leida en `chat_read_receipts`.

### Embajador: reportes e IA

- `116. GET /api/ambassadors/reports/performance`
  - Devuelve reporte mensual con clics, leads, conversiones, conversion rate y comisiones.
  - Agrega datos de `ambassador_referral_links`, `ambassador_leads`, `ambassador_referrals` y `ambassador_commissions`.

- `117. GET /api/ambassadors/reports/referrals`
  - Lista reporte de referidos con estado, tipo, ventas generadas y comision asociada.

- `118. GET /api/ambassadors/reports/commissions`
  - Devuelve resumen de comisiones por periodo reutilizando el calculo de `commissions/summary`.

- `119. GET /api/ambassadors/reports/conversion-funnel`
  - Devuelve embudo de conversion: clics, leads, registros y activos.

- `120. GET /api/ambassadors/reports/export`
  - Devuelve URL deterministica de descarga del reporte mensual.

- `121. POST /api/ambassadors/ai/query`
  - Responde consulta del asistente IA con resumen, acciones y foco.
  - La respuesta es deterministica y basada en el contexto de embajador.

- `122. GET /api/ambassadors/ai/insights`
  - Devuelve radar de calidad de leads, seguimiento, conversion y potencial de comisiones.

- `123. POST /api/ambassadors/ai/prospect-score`
  - Calcula score de prospecto desde `ambassador_leads.close_probability`.
  - Responde con probabilidad de cierre y motivos.

- `124. POST /api/ambassadors/ai/follow-up-suggestion`
  - Genera mensaje sugerido de seguimiento para un referido `BUS-{uuid}`.

- `125. POST /api/ambassadors/ai/improvement-plan`
  - Devuelve plan de mejora con objetivo, acciones y tiempo estimado.

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

- `171. GET /api/specialists/ai/insights`
  - Devuelve radar de insights operativos.
  - Respuesta deterministica sin proveedor externo.

- `172. POST /api/specialists/ai/pricing-suggestion`
  - Sugiere precio a partir del precio actual enviado.
  - Calcula rango minimo, maximo y precio recomendado.

- `173. POST /api/specialists/ai/improvement-plan`
  - Devuelve plan de mejora para el area solicitada.
  - Respuesta deterministica con objetivo, acciones y tiempo estimado.

- `174. POST /api/specialists/ai/schedule-optimization`
  - Devuelve sugerencia para optimizar agenda y plan sugerido.
  - No modifica agenda; solo entrega recomendacion.

### Busqueda general

- `175. GET /api/search/global`
  - Busca por `q` en productos/listings, empresas, servicios de especialistas y comunidades.
  - Lee tablas existentes con `SearchJdbcRepository`.
  - Responde `total` y `resultados`.

- `176. GET /api/search/suggestions`
  - Devuelve sugerencias por `q`.
  - Si no llega `q`, usa tendencias persistidas.

- `177. GET /api/search/trending`
  - Lista busquedas populares desde `search_trends`.
  - Si no hay datos persistidos, devuelve tendencias por defecto.

- `178. GET /api/search/history`
  - Lista historial de busquedas del usuario autenticado.
  - Usa header `X-User-Id`.

- `179. POST /api/search/history`
  - Guarda una busqueda del usuario autenticado.
  - Persiste en `search_history` e incrementa `search_trends`.

- `180. DELETE /api/search/history/{historyId}`
  - Elimina una busqueda propia del historial.
  - Acepta `SRH-{uuid}` o UUID crudo.

## Persistencia agregada

Se agregaron las migraciones:

- `TechMarket-IA/src/main/resources/db/migration/V77__specialist_profile_services_portfolio_availability.sql`
- `TechMarket-IA/src/main/resources/db/migration/V78__specialist_calendar_blocks.sql`
- `TechMarket-IA/src/main/resources/db/migration/V79__specialist_files_and_transactions.sql`
- `TechMarket-IA/src/main/resources/db/migration/V80__specialist_withdrawals_certifications_ai_reviews.sql`
- `TechMarket-IA/src/main/resources/db/migration/V81__global_search_history.sql`

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

- `search_history`
  - Historial de busquedas por usuario.
  - Campos principales: `id`, `user_id`, `query_text`, `result_type`, `searched_at`.

- `search_trends`
  - Terminos populares de busqueda.
  - Campos principales: `id`, `query_text`, `search_count`, `updated_at`.

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
- `idx_search_history_user_date`
- `idx_search_trends_count`

## Codigo agregado

### Helpers de aplicacion

- `SpecialistIdentitySupport`
  - Valida `X-User-Id`.
  - Devuelve `401` si falta el header.
  - Devuelve `400` si el UUID es invalido.
  - Devuelve `404` si el usuario no existe.
  - Formatea IDs `TEC-`, `SERV-`, `PORT-`, `BLK-`, `REQ-`, `PROJ-`, `CHT-`, `MSG-`, `FILE-`, `TX-` y `CERT-`.

- `SearchIdentitySupport`
  - Valida `X-User-Id` para historial de busqueda.
  - Permite parsear IDs `SRH-`.
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
- `SearchHistoryJpaEntity`
- `SearchTrendJpaEntity`

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
- `SearchHistorySpringDataRepository`
- `SearchTrendSpringDataRepository`
- `SearchJdbcRepository`

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
  - Maneja consulta, insights, pricing, mejora y optimizacion de agenda con respuestas deterministicas.

- `SearchController`
  - Maneja busqueda global, sugerencias, tendencias e historial de busqueda.

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
- `POST /api/specialists/ai/pricing-suggestion` calcula sugerencia de precio.
- `POST /api/search/history` guarda historial e incrementa tendencias.

## Actualizacion 181-200

Se agrego el bloque general de notificaciones, conversaciones y pagos/transacciones en `TechMarket-IA`.

### Notificaciones generales

- `181. GET /api/notifications`
  - Lista notificaciones del usuario autenticado.
  - Reutiliza `notifications` y devuelve `NOT-{uuid}`.

- `182. GET /api/notifications/unread-count`
  - Devuelve `{ "noLeidas": n }`.

- `183. PUT /api/notifications/{notificationId}/read`
  - Marca una notificacion propia como leida.

- `184. PUT /api/notifications/read-all`
  - Marca todas las notificaciones propias como leidas.

- `185. DELETE /api/notifications/{notificationId}`
  - Elimina una notificacion propia.

- `186. GET /api/notifications/preferences`
  - Devuelve preferencias `email`, `push` e `inApp`.
  - Si no existen, crea valores por defecto en `user_notification_preferences`.

- `187. PUT /api/notifications/preferences`
  - Actualiza preferencias de notificacion.

### Mensajeria unificada

- `188. GET /api/conversations`
  - Lista conversaciones donde el usuario es cliente o tecnico asignado.
  - Reutiliza `tickets` con `ticket_type = CHAT`.

- `189. POST /api/conversations`
  - Crea conversacion con empresa (`EMP-{uuid}`) o especialista (`TEC-{uuid}`) y mensaje inicial.

- `190. GET /api/conversations/{conversationId}/messages`
  - Lista mensajes de una conversacion propia.

- `191. POST /api/conversations/{conversationId}/messages`
  - Agrega mensaje a una conversacion propia.

- `192. PUT /api/conversations/{conversationId}/read`
  - Registra lectura en `chat_read_receipts`.

- `193. DELETE /api/messages/{messageId}`
  - Elimina un mensaje cuyo autor sea el usuario autenticado.

### Pagos y transacciones

- `194. GET /api/payments/methods`
  - Lista metodos de pago guardados en `user_payment_methods`.

- `195. POST /api/payments/methods`
  - Agrega metodo de pago con token de referencia y devuelve `PM-{uuid}`.

- `196. DELETE /api/payments/methods/{paymentMethodId}`
  - Elimina metodo propio.

- `197. POST /api/payments/intents`
  - Crea intencion de pago `PAY-{uuid}` en estado `pendiente`.

- `198. POST /api/payments/confirm`
  - Confirma intencion propia y crea transaccion `TRX-{uuid}`.

- `199. GET /api/transactions`
  - Lista transacciones del usuario autenticado.

- `200. GET /api/transactions/{transactionId}`
  - Devuelve detalle de una transaccion propia.

### Persistencia agregada para 181-200

- Nueva migracion: `V82__general_notifications_conversations_payments.sql`.
- Nuevas tablas:
  - `user_notification_preferences`
  - `user_payment_methods`
  - `user_payment_intents`
  - `user_transactions`
- Tablas reutilizadas:
  - `notifications`
  - `tickets`
  - `ticket_messages`
  - `chat_read_receipts`

## Actualizacion 201-220

Se agrego el bloque general de facturas, reembolsos, soporte, reportes, configuracion publica y endpoints admin iniciales.

### Facturas y reembolsos

- `201. GET /api/invoices`
  - Lista facturas/comprobantes del usuario autenticado.
  - Lee desde `user_invoices` y devuelve `INV-{uuid}`.

- `202. GET /api/invoices/{invoiceId}/download`
  - Devuelve `downloadUrl` de una factura propia.

- `203. POST /api/payments/refunds`
  - Solicita reembolso sobre una transaccion propia `TRX-{uuid}`.
  - Persiste en `user_payment_refunds` con estado `pendiente_revision`.

### Soporte

- `204. POST /api/support/tickets`
  - Crea ticket de soporte en `tickets` con `ticket_type = SUPPORT`.
  - Devuelve `TCK-{uuid}`.

- `205. GET /api/support/tickets`
  - Lista tickets de soporte del usuario autenticado.

- `206. GET /api/support/tickets/{ticketId}`
  - Devuelve detalle de un ticket propio.

- `207. POST /api/support/tickets/{ticketId}/messages`
  - Crea mensaje en `ticket_messages`.
  - Devuelve `TMSG-{uuid}`.

- `208. PUT /api/support/tickets/{ticketId}/status`
  - Actualiza estado de un ticket de soporte propio.

### Reportes

- `209. POST /api/reports`
  - Crea reporte de usuario en `user_reports`.
  - Devuelve `REP-{uuid}` con estado `pendiente_revision`.

- `210. GET /api/reports`
  - Lista reportes creados por el usuario.

- `211. GET /api/reports/{reportId}`
  - Devuelve detalle de un reporte propio.

### Configuracion publica

- `212. GET /api/config/countries`
  - Devuelve Bolivia como pais disponible.

- `213. GET /api/config/countries/{countryCode}/cities`
  - Devuelve ciudades disponibles para `BO`.

- `214. GET /api/config/currencies`
  - Devuelve `BOB` y `USD`.

- `215. GET /api/config/categories`
  - Lista categorias raiz desde `catalog_categories` con subcategorias.

- `216. GET /api/config/user-types`
  - Devuelve tipos de usuario permitidos.

- `217. GET /api/config/platform`
  - Devuelve configuracion publica de TechMarket.

- `218. GET /api/config/payment-options`
  - Devuelve tarjeta, QR y transferencia.

### Admin inicial

- `219. GET /api/admin/dashboard`
  - Devuelve conteos de usuarios, empresas, reportes pendientes y transacciones del dia.
  - Lee desde `users`, `tenants`, `user_reports` y `user_transactions`.

- `220. GET /api/admin/users`
  - Lista usuarios registrados desde `users`.

### Persistencia agregada para 201-220

- Nueva migracion: `V83__general_billing_support_reports.sql`.
- Nuevas tablas:
  - `user_invoices`
  - `user_payment_refunds`
  - `user_reports`
- Tablas reutilizadas:
  - `tickets`
  - `ticket_messages`
  - `catalog_categories`
  - `users`
  - `tenants`
  - `user_transactions`

## Actualizacion 221-226

Se agrego el cierre del bloque admin y moderacion en `TechMarket-IA`.

### Admin y moderacion

- `221. PUT /api/admin/users/{userId}/status`
  - Actualiza estado de usuario en `users.status`.
  - Acepta `USR-{uuid}` o UUID crudo.
  - Registra auditoria en `audit_logs`.

- `222. GET /api/admin/reports`
  - Lista reportes con estado `pendiente_revision`.
  - Lee desde `user_reports`.

- `223. PUT /api/admin/reports/{reportId}/status`
  - Actualiza estado de un reporte y guarda la accion aplicada.
  - Persiste `user_reports.status` y `user_reports.admin_action`.
  - Registra auditoria en `audit_logs`.

- `224. GET /api/admin/moderation/queue`
  - Expone cola de moderacion basada en reportes pendientes.
  - Devuelve IDs `MOD-{uuid}` y prioridad calculada por motivo.

- `225. POST /api/admin/moderation/actions`
  - Registra accion de moderacion en `moderation_actions`.
  - Devuelve `ACT-{uuid}`.
  - Registra auditoria en `audit_logs`.

- `226. GET /api/admin/audit-logs`
  - Lista las ultimas 50 acciones administrativas desde `audit_logs`.
  - Devuelve IDs `AUD-{uuid}` y actores `ADM-{uuid}` cuando existe actor.

### Persistencia usada para 221-226

- Tablas reutilizadas:
  - `users`
  - `user_reports`
  - `moderation_actions`
  - `audit_logs`
- Extension agregada en `V83__general_billing_support_reports.sql`:
  - `user_reports.admin_action`

## Persistencia Ambassador 56-125

- `V84__ambassador_profile_referral_links.sql`
  - Extiende `ambassadors` con datos de perfil, avatar y configuracion.
  - Agrega `ambassador_referral_links`.

- `V85__ambassador_referrals_onboarding_leads.sql`
  - Extiende `ambassador_referrals` con datos de prospecto/contacto.
  - Agrega `ambassador_referral_activity`, `ambassador_referral_notes`, `ambassador_referral_files`.
  - Agrega `ambassador_onboarding_tasks`, `ambassador_onboarding_reminders`, `ambassador_onboarding_milestones`.
  - Agrega `ambassador_leads`.

- `V86__ambassador_commission_disputes_withdrawals.sql`
  - Agrega `ambassador_commission_disputes` para disputas de comisiones.
  - Agrega `ambassador_withdrawals` para solicitudes de retiro del wallet.
  - Los endpoints de comisiones leen `ambassador_commissions`.

- `V87__ambassador_payout_methods_network_chats.sql`
  - Agrega `ambassador_payout_methods` para metodos de pago de retiros.
  - Agrega `ambassador_invitations` para invitaciones de red.
  - Extiende `ambassadors` con `sponsor_ambassador_id` para red directa.
  - Los chats de embajador reutilizan `tickets`, `ticket_messages` y `chat_read_receipts`.

- Endpoints 116-125
  - No agregan migracion nueva.
  - Reportes agregan datos existentes de links, leads, referidos y comisiones.
  - IA de embajador usa respuestas deterministicas con contexto de leads/referidos.

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
