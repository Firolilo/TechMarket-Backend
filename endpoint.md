ENDPOINTS TECHMARKET

1. AUTENTICACIÓN (8 endpoints)
Registro, login, logout, refresh token, recuperar contraseña, verificar email
Perfil de usuario y configuración

POST auth/register
Registrar nuevo usuario en el sistema.

{
  "email": "usuario@example.com",
  "password": "SecurePass123!",
  "confirmPassword": "SecurePass123!",
  "tipo": "cliente",
  "nombre": "Juan Pérez",
  "apellido": "García",
  "telefono": "+56912345678",
  "pais": "Bolivia",
  "ciudad": "Santa Cruz",
  "terminos": true
}
Response (201 Created):

{
  "id": "USR-001",
  "email": "usuario@example.com",
  "nombre": "Juan Pérez",
  "tipo": "cliente",
  "estado": "Pendiente verificación",
  "mensaje": "Verificar email enviado a usuario@example.com"
}

POST auth/login Autenticar usuario y obtener token JWT.
{
  "email": "usuario@example.com",
  "password": "SecurePass123!",
  "tipo": "cliente"
}
Response 200 ok
{
  "usuario": {
    "id": "USR-001",
    "email": "usuario@example.com",
    "nombre": "Juan Pérez",
    "tipo": "cliente",
    "estado": "Activo"
  },
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600

}
POST auth/logout
Headers: Authorization: Bearer {token}

Response (200 OK):
{
  "mensaje": "Sesión cerrada exitosamente",
  "timestamp": "2026-04-28T15:30:00Z"
}

POST auth/refresh-token
Refrescar token JWT expirado.

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
Response 200
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600
}

POST auth/forgot-password
Solicitar recuperación de contraseña.

{
  "email": "usuario@example.com"
}

Perfil de Usuario

GET users/profile
Obtener perfil del usuario autenticado.
Headers: Authorization: Bearer {token}
Response (200 OK):
{
  "id": "USR-001",
  "email": "usuario@example.com",
  "nombre": "Juan",
  "apellido": "Pérez",
  "tipo": "cliente",
  "telefono": "+56912345678",
  "pais": "Bolivia",
  "ciudad": "Santa Cruz",
  "avatar": "https://techmarket.bo/avatars/usr-001.jpg",
  "estado": "Activo",
  "fechaRegistro": "2026-04-01T10:00:00Z",
  "ultimaActualizacion": "2026-04-28T14:20:00Z"
}
PUT users/profile
Actualizar perfil del usuario autenticado.
{
  "nombre": "Juan",
  "apellido": "Pérez García",
  "telefono": "+56987654321",
  "ciudad": "La Paz",
  "descripcion": "Comprador de productos tecnológicos"
}
GET users/public/:userId
Obtener perfil público de otro usuario.
{
  "id": "USR-002",
  "nombre": "Carlos",
  "apellido": "López",
  "tipo": "empresa",
  "ciudad": "Santa Cruz",
  "avatar": "https://techmarket.bo/avatars/usr-002.jpg",
  "verificado": true,
  "reputacion": 4.8,
  "totalReseñas": 245
}




2. MÓDULO CLIENTE (50+ endpoints)
Perfil y direcciones
GET /api/clients/profile 
Obtener los datos del perfil del cliente autenticado.
Response (200 OK):
json
{
 "id": "USR-001",
 "email": "usuario@example.com",
 "nombre": "Juan",
 "apellido": "Pérez",
 "telefono": "+56912345678",
 "avatar": "https://techmarket.bo/avatars/usr-001.jpg"
}
PUT /api/clients/profile 
Actualizar información del perfil.
Request:
json
{
 "nombre": "Juan Carlos",
 "apellido": "Pérez García",
 "telefono": "+56987654321",
 "avatar": "https://techmarket.bo/avatars/new-avatar.jpg"
}
Response (200 OK): (Retorna el perfil actualizado con la misma estructura del GET)
GET /api/clients/addresses 
Listar todas las direcciones guardadas del cliente.
Response (200 OK):
json
[
 {
   "id": "ADDR-101",
   "titulo": "Casa",
   "pais": "Bolivia",
   "ciudad": "Santa Cruz",
   "direccion": "Av. Equipetrol, Calle 5, #123",
   "referencia": "Portón negro al lado de la farmacia",
   "esPredeterminada": true
 }
]
POST /api/clients/addresses 
Crear una nueva dirección.
Request:
json
{
 "titulo": "Oficina",
 "pais": "Bolivia",
 "ciudad": "Santa Cruz",
 "direccion": "Torre Empresarial, Piso 4, Of 402",
 "referencia": "Frente a la plazuela",
 "esPredeterminada": false
}
Response (201 Created): (Retorna la nueva dirección con su id generado)
PUT /api/clients/addresses/:addressId 
Actualizar una dirección existente.
Request:
json
{
 "direccion": "Av. Equipetrol, Calle 5, #125 (Nueva numeración)"
}
Response (200 OK): (Retorna la dirección actualizada)
DELETE /api/clients/addresses/:addressId 
Eliminar una dirección.
Response (200 OK):
json
{ "mensaje": "Dirección eliminada correctamente" }
PUT /api/clients/addresses/:addressId/default 
Establecer una dirección como predeterminada.
Response (200 OK):
json
{ "mensaje": "Dirección establecida como predeterminada" }

Marketplace y búsqueda (productos, categorías, empresas)
GET /api/marketplace/products 
Listar productos (con query params: ?search=laptop&category=tech).

Response (200 OK):
json
{
  "total": 145,
  "pagina": 1,
  "productos": [
    {
      "id": "PROD-992",
      "nombre": "Laptop Gamer ASUS ROG",
      "precio": 1200.50,
      "imagenPrincipal": "https://techmarket.bo/img/prod-992.jpg",
      "calificacion": 4.8
    }
  ]
}
GET /api/marketplace/products/:productId 
Obtener el detalle completo de un producto.

Response (200 OK):
json
{
  "id": "PROD-992",
  "nombre": "Laptop Gamer ASUS ROG",
  "descripcion": "Laptop de alto rendimiento para gaming...",
  "precio": 1200.50,
  "imagenes": ["img1.jpg", "img2.jpg"],
  "empresa": { "id": "EMP-005", "nombre": "ElectroMundo" },
  "stock": 15
}
GET /api/marketplace/categories 
Listar el árbol de categorías disponibles.

Response (200 OK):
json
[
  {
    "id": "CAT-001",
    "nombre": "Computación",
    "subcategorias": [
      { "id": "CAT-001-1", "nombre": "Laptops" },
      { "id": "CAT-001-2", "nombre": "Accesorios" }
    ]
  }
]

GET /api/marketplace/categories/:categoryId/products 
Listar productos de una categoría específica.

Response (200 OK): (Retorna la misma estructura que GET /api/marketplace/products)
GET /api/marketplace/companies 
Listar las empresas/tiendas registradas.

Response (200 OK):
json
[
  {
    "id": "EMP-005",
    "nombre": "ElectroMundo",
    "logo": "logo.jpg",
    "calificacion": 4.9
  }
]
GET /api/marketplace/companies/:companyId 
Obtener el perfil público de una empresa.

Response (200 OK):
json
{
  "id": "EMP-005",
  "nombre": "ElectroMundo",
  "descripcion": "Especialistas en gaming",
  "fechaRegistro": "2024-01-01",
  "ventasCompletadas": 1500
}
GET /api/marketplace/companies/:companyId/products 
Listar el catálogo de productos de una empresa.

Response (200 OK): (Retorna estructura de paginación de productos)
Órdenes y compras
GET /api/clients/cart 
Obtener el estado actual del carrito.

Response (200 OK):
json
{
  "id": "CART-001",
  "subtotal": 1200.50,
  "items": [
    {
      "id": "ITEM-1",
      "productoId": "PROD-992",
      "cantidad": 1,
      "precioUnitario": 1200.50
    }
  ]
}
POST /api/clients/cart/items 
Agregar un producto al carrito.

Request:
json
{ "productoId": "PROD-104", "cantidad": 2 }
Response (200 OK): (Retorna el carrito actualizado)
PUT /api/clients/cart/items/:itemId 
Actualizar la cantidad de un ítem.

Request:
json
{ "cantidad": 3 }
Response (200 OK): (Retorna el carrito actualizado)
DELETE /api/clients/cart/items/:itemId 
Eliminar un ítem del carrito.

Response (200 OK): (Retorna el carrito actualizado)
DELETE /api/clients/cart 
Vaciar el carrito completo.

Response (200 OK):
json
{ "mensaje": "Carrito vaciado correctamente" }
POST /api/clients/checkout 
Procesar la compra.

Request:
json
{
  "direccionEnvioId": "ADDR-101",
  "metodoPago": "tarjeta_credito"
}
Response (201 Created):
json
{
  "ordenId": "ORD-2026-0001",
  "estado": "PendientePago",
  "total": 1215.50
}
GET /api/clients/orders 
Listar el historial de órdenes.

Response (200 OK):
json
[
  {
    "id": "ORD-2026-0001",
    "fechaCreacion": "2026-05-03T10:30:00Z",
    "estado": "En tránsito",
    "total": 1215.50
  }
]
GET /api/clients/orders/:orderId 
Obtener el detalle completo de una orden.

Response (200 OK):
json
{
  "id": "ORD-2026-0001",
  "estado": "En tránsito",
  "total": 1215.50,
  "items": [{ "productoId": "PROD-992", "cantidad": 1 }],
  "tracking": { "codigo": "TRK-999888", "empresa": "FedEx" }
}
PUT /api/clients/orders/:orderId/cancel
Solicitar la cancelación de una orden.

Request:
json
{ "motivo": "Me arrepentí de la compra" }
Response (200 OK):
json
{ "mensaje": "Orden cancelada", "nuevoEstado": "Cancelada" }
Reseñas y valoraciones
POST /api/clients/reviews/products/:productId 
Crear una reseña de producto.

Request:
json
{ "calificacion": 5, "comentario": "Excelente equipo" }
Response (201 Created):
json
{ "id": "REV-050", "mensaje": "Reseña publicada" }
PUT /api/clients/reviews/:reviewId 
Editar una reseña existente.

Request:
json
{ "calificacion": 4, "comentario": "Buen equipo, pero tardó el envío" }
Response (200 OK): (Retorna la reseña actualizada)
DELETE /api/clients/reviews/:reviewId 
Eliminar una reseña.

Response (200 OK):
json
{ "mensaje": "Reseña eliminada" }
POST /api/clients/reviews/companies/:companyId 
Calificar a una empresa.

Request:
json
{ "calificacion": 5, "comentario": "Excelente atención al cliente" }
Response (201 Created): (Mismo formato que reseña de producto)
GET /api/marketplace/products/:productId/reviews 
Leer las reseñas de un producto.

Response (200 OK):
json
[
  {
    "id": "REV-050",
    "cliente": { "nombre": "Juan P.", "avatar": "avatar.jpg" },
    "calificacion": 5,
    "comentario": "Excelente equipo",
    "fecha": "2026-05-01"
  }
]
Chat y comunicación
GET /api/clients/chats 
Listar conversaciones activas.

Response (200 OK):
json
[
  {
    "id": "CHT-884",
    "empresa": { "nombre": "ElectroMundo" },
    "ultimoMensaje": "Sí, el envío es gratis.",
    "mensajesSinLeer": 1
  }
]
POST /api/clients/chats 
Iniciar una nueva conversación.

Request:
json
{ "empresaId": "EMP-005", "asunto": "Consulta producto" }
Response (201 Created):
json
{ "chatId": "CHT-885", "estado": "Abierto" }
GET /api/clients/chats/:chatId/messages 
Obtener el historial de mensajes.

Response (200 OK):
json
[
  {
    "id": "MSG-01",
    "remitente": "cliente",
    "contenido": "Hola, ¿tienen stock?",
    "fecha": "2026-05-03T10:00:00Z"
  }
]
POST /api/clients/chats/:chatId/messages 
Enviar un mensaje.

Request:
json
{ "contenido": "Gracias por la información" }
Response (201 Created): (Retorna el objeto del mensaje enviado)
PUT /api/clients/chats/:chatId/read 
Marcar mensajes como leídos.

Response (200 OK):
json
{ "mensaje": "Chat marcado como leído" }
Comunidades y favoritos
GET /api/clients/favorites/products 
Listar productos favoritos.

Response (200 OK): (Retorna array de productos reducidos)
POST /api/clients/favorites/products/:productId 
Agregar producto a favoritos.

Response (200 OK):
json
{ "mensaje": "Agregado a favoritos" }
DELETE /api/clients/favorites/products/:productId
Quitar producto de favoritos.

Response (200 OK):
json
{ "mensaje": "Removido de favoritos" }
GET /api/clients/favorites/companies 
Listar empresas seguidas.

Response (200 OK): (Retorna array de empresas reducidas)
POST /api/clients/favorites/companies/:companyId 
Seguir a una empresa.

Response (200 OK):
json
{ "mensaje": "Ahora sigues a esta empresa" }
DELETE /api/clients/favorites/companies/:companyId 
Dejar de seguir empresa.

Response (200 OK):
json
{ "mensaje": "Dejaste de seguir a esta empresa" }
GET /api/clients/communities 
Listar comunidades del cliente.

Response (200 OK):
json
[
  { "id": "COM-01", "nombre": "PC Gamers Bolivia", "miembros": 1500 }
]
POST /api/clients/communities/:communityId/join 
Unirse a comunidad.

Response (200 OK):
json
{ "mensaje": "Te has unido a la comunidad" }
DELETE /api/clients/communities/:communityId/leave 
Salir de comunidad.

Response (200 OK):
json
{ "mensaje": "Has salido de la comunidad" }
GET /api/clients/communities/:communityId/posts 
Ver posts de una comunidad.

Response (200 OK):
json
[
  { "id": "POST-01", "autor": "Carlos", "contenido": "¿Qué tarjeta gráfica recomiendan?" }
]
Notificaciones
GET /api/clients/notifications 
Listar notificaciones.

Response (200 OK):
json
[
  {
    "id": "NOT-123",
    "titulo": "Orden enviada",
    "leido": false,
    "enlace": "/ordenes/1"
  }
]
PUT /api/clients/notifications/:notificationId/read 
Marcar una notificación como leída.

Response (200 OK):
json
{ "id": "NOT-123", "leido": true }
PUT /api/clients/notifications/read-all 
Marcar todas como leídas.

Response (200 OK):
json
{ "mensaje": "Todas las notificaciones marcadas como leídas" }
DELETE /api/clients/notifications/:notificationId 
Eliminar una notificación.

Response (200 OK):
json
{ "mensaje": "Notificación eliminada" }
3. MÓDULO EMPRESA (60+ endpoints)
Perfil y configuración
Gestión de productos y publicaciones
Campañas publicitarias
Chat con clientes y leads
Órdenes y ventas
Analytics y reportes
Gestión de equipo
Integración IA para insights y recomendaciones

4. MÓDULO EMBAJADOR (50+ endpoints)
Perfil y configuración
GET /api/ambassadors/profile
Obtener perfil del embajador autenticado.
Response 200:
{
 "id": "AMB-001",
 "email": "embajador@example.com",
 "nombre": "María",
 "apellido": "Rojas",
 "telefono": "+59171234567",
 "pais": "Bolivia",
 "ciudad": "Santa Cruz",
 "avatar": "https://techmarket.bo/avatars/amb-001.jpg",
 "estado": "Activo",
 "nivel": "Gold",
 "codigoReferido": "MARIA-GOLD"}
PUT /api/ambassadors/profile
Actualizar perfil del embajador.
Request:
{
 "nombre": "María Fernanda",
 "apellido": "Rojas",
 "telefono": "+59179876543",
 "ciudad": "La Paz",
 "descripcion": "Embajadora especializada en negocios tecnológicos"}
Response 200:
{
 "mensaje": "Perfil actualizado correctamente"}
POST /api/ambassadors/profile/photo
Subir o cambiar foto de perfil.
Response 200:
{
 "url": "https://cdn.techmarket.bo/ambassadors/amb-001-avatar.jpg"}
GET /api/ambassadors/profile/stats
Obtener métricas principales del embajador.
Response 200:
{
 "negociosReferidos": 48,
 "negociosActivos": 31,
 "conversionRate": 64.5,
 "comisionesTotales": "Bs 18.500",
 "nivel": "Gold"}
GET /api/ambassadors/settings
Obtener configuración del embajador.
Response 200:
{
 "notificacionesEmail": true,
 "notificacionesPush": true,
 "mostrarPerfilPublico": true,
 "idioma": "es"}
PUT /api/ambassadors/settings
Actualizar configuración del embajador.
Request:
{
 "notificacionesEmail": true,
 "notificacionesPush": false,
 "mostrarPerfilPublico": true}
Response 200:
{
 "mensaje": "Configuración actualizada"}
Referral links y códigos
GET /api/ambassadors/referral-links
Listar links de referido creados por el embajador.
Response 200:
[
 {
   "id": "REFLINK-001",
   "nombre": "Campaña empresas Santa Cruz",
   "codigo": "MARIA-SCZ",
   "url": "https://techmarket.bo/register?ref=MARIA-SCZ",
   "clics": 320,
   "conversiones": 24,
   "activo": true
 }]
POST /api/ambassadors/referral-links
Crear nuevo link de referido.
Request:
{
 "nombre": "Campaña técnicos La Paz",
 "segmento": "especialistas",
 "ciudad": "La Paz"}
Response 201:
{
 "id": "REFLINK-002",
 "codigo": "MARIA-LPZ",
 "url": "https://techmarket.bo/register?ref=MARIA-LPZ"}
GET /api/ambassadors/referral-links/:linkId
Obtener detalle de un link de referido.
Response 200:
{
 "id": "REFLINK-001",
 "nombre": "Campaña empresas Santa Cruz",
 "codigo": "MARIA-SCZ",
 "url": "https://techmarket.bo/register?ref=MARIA-SCZ",
 "clics": 320,
 "conversiones": 24,
 "conversionRate": 7.5,
 "activo": true}
PUT /api/ambassadors/referral-links/:linkId
Actualizar un link de referido.
Request:
{
 "nombre": "Campaña empresas premium Santa Cruz",
 "segmento": "empresas"}
Response 200:
{
 "mensaje": "Link actualizado correctamente"}
PATCH /api/ambassadors/referral-links/:linkId/status
Activar o desactivar link.
Request:
{
 "activo": false}
Response 200:
{
 "id": "REFLINK-001",
 "activo": false}
DELETE /api/ambassadors/referral-links/:linkId
Eliminar link de referido.
Response 200:
{
 "mensaje": "Link de referido eliminado"}
GET /api/ambassadors/referral-links/:linkId/qr
Generar QR del link de referido.
Response 200:
{
 "qrUrl": "https://cdn.techmarket.bo/qr/reflink-001.png"}
GET /api/ambassadors/referral-codes
Listar códigos activos del embajador.
Response 200:
[
 {
   "codigo": "MARIA-GOLD",
   "tipo": "general",
   "usos": 58,
   "activo": true
 }]
Negocios referidos
GET /api/ambassadors/referrals
Listar negocios o usuarios referidos.
Response 200:
[
 {
   "id": "BUS-001",
   "nombre": "ElectroMundo",
   "tipo": "empresa",
   "estado": "activo",
   "fechaRegistro": "2026-04-10",
   "comisionGenerada": "Bs 850"
 }]
POST /api/ambassadors/referrals
Registrar manualmente un prospecto referido.
Request:
{
 "nombre": "FixCloud Bolivia",
 "tipo": "empresa",
 "contacto": "Carlos Méndez",
 "telefono": "+59170001122",
 "email": "contacto@fixcloud.bo",
 "ciudad": "Santa Cruz"}
Response 201:
{
 "id": "BUS-002",
 "estado": "prospecto",
 "mensaje": "Prospecto registrado correctamente"}
GET /api/ambassadors/referrals/:referralId
Obtener detalle de un referido.
Response 200:
{
 "id": "BUS-001",
 "nombre": "ElectroMundo",
 "tipo": "empresa",
 "estado": "activo",
 "contacto": {
   "nombre": "Carlos Méndez",
   "telefono": "+59170001122",
   "email": "contacto@electromundo.bo"
 },
 "fechaRegistro": "2026-04-10",
 "ultimaActividad": "2026-05-01T14:20:00Z"}
PUT /api/ambassadors/referrals/:referralId
Actualizar datos de un referido.
Request:
{
 "contacto": "Carlos A. Méndez",
 "telefono": "+59171112233",
 "ciudad": "La Paz"}
Response 200:
{
 "mensaje": "Referido actualizado correctamente"}
PATCH /api/ambassadors/referrals/:referralId/status
Actualizar estado del referido.
Request:
{
 "estado": "en_onboarding"}
Response 200:
{
 "id": "BUS-001",
 "estado": "en_onboarding"}
DELETE /api/ambassadors/referrals/:referralId
Eliminar referido prospecto.
Response 200:
{
 "mensaje": "Referido eliminado correctamente"}
GET /api/ambassadors/referrals/:referralId/activity
Obtener actividad del referido.
Response 200:
[
 {
   "id": "ACT-001",
   "tipo": "registro",
   "descripcion": "Empresa completó registro inicial",
   "fecha": "2026-04-10T10:00:00Z"
 }]
POST /api/ambassadors/referrals/:referralId/notes
Agregar nota interna al referido.
Request:
{
 "nota": "Interesado en vender accesorios gaming."}
Response 201:
{
 "id": "NOTE-001",
 "mensaje": "Nota agregada"}
GET /api/ambassadors/referrals/:referralId/notes
Listar notas del referido.
Response 200:
[
 {
   "id": "NOTE-001",
   "nota": "Interesado en vender accesorios gaming.",
   "fecha": "2026-04-29T15:00:00Z"
 }]
POST /api/ambassadors/referrals/:referralId/files
Subir archivo asociado al referido.
Response 201:
{
 "id": "FILE-001",
 "url": "https://cdn.techmarket.bo/referrals/file-001.pdf"}
Seguimiento de onboarding
GET /api/ambassadors/onboarding
Listar procesos de onboarding activos.
Response 200:
[
 {
   "id": "ONB-001",
   "referidoId": "BUS-001",
   "nombre": "ElectroMundo",
   "progreso": 75,
   "estado": "en_proceso"
 }]
GET /api/ambassadors/onboarding/:onboardingId
Obtener detalle del onboarding.
Response 200:
{
 "id": "ONB-001",
 "referido": "ElectroMundo",
 "estado": "en_proceso",
 "progreso": 75,
 "pasos": [
   {
     "id": "STEP-001",
     "nombre": "Completar perfil",
     "completado": true
   },
   {
     "id": "STEP-002",
     "nombre": "Publicar primer producto",
     "completado": false
   }
 ]}
POST /api/ambassadors/onboarding/:onboardingId/tasks
Crear tarea de seguimiento.
Request:
{
 "titulo": "Ayudar a publicar primer producto",
 "fechaLimite": "2026-05-10"}
Response 201:
{
 "id": "TASK-001",
 "mensaje": "Tarea creada"}
GET /api/ambassadors/onboarding/:onboardingId/tasks
Listar tareas de onboarding.
Response 200:
[
 {
   "id": "TASK-001",
   "titulo": "Ayudar a publicar primer producto",
   "estado": "pendiente",
   "fechaLimite": "2026-05-10"
 }]
PATCH /api/ambassadors/onboarding/tasks/:taskId/status
Actualizar estado de una tarea.
Request:
{
 "estado": "completada"}
Response 200:
{
 "id": "TASK-001",
 "estado": "completada"}
POST /api/ambassadors/onboarding/:onboardingId/reminders
Crear recordatorio de seguimiento.
Request:
{
 "fecha": "2026-05-08T09:00:00Z",
 "mensaje": "Contactar para revisar avance del catálogo"}
Response 201:
{
 "id": "REM-001",
 "mensaje": "Recordatorio creado"}
GET /api/ambassadors/onboarding/milestones
Listar hitos de onboarding disponibles.
Response 200:
[
 {
   "id": "MLS-001",
   "nombre": "Registro completado",
   "orden": 1
 },
 {
   "id": "MLS-002",
   "nombre": "Primera publicación",
   "orden": 2
 }]
PATCH /api/ambassadors/onboarding/:onboardingId/milestones/:milestoneId
Marcar hito como completado.
Response 200:
{
 "mensaje": "Hito marcado como completado"}
Prospectos y leads
GET /api/ambassadors/leads
Listar leads capturados.
Response 200:
[
 {
   "id": "LEAD-001",
   "nombre": "TecnoStore Bolivia",
   "tipo": "empresa",
   "estado": "nuevo",
   "fuente": "evento"
 }]
POST /api/ambassadors/leads
Crear nuevo lead.
Request:
{
 "nombre": "TecnoStore Bolivia",
 "tipo": "empresa",
 "contacto": "Ana López",
 "telefono": "+59175556677",
 "fuente": "evento"}
Response 201:
{
 "id": "LEAD-001",
 "mensaje": "Lead creado correctamente"}
GET /api/ambassadors/leads/:leadId
Obtener detalle de un lead.
Response 200:
{
 "id": "LEAD-001",
 "nombre": "TecnoStore Bolivia",
 "tipo": "empresa",
 "estado": "nuevo",
 "probabilidadCierre": 68}
PUT /api/ambassadors/leads/:leadId
Actualizar lead.
Response 200:
{
 "mensaje": "Lead actualizado"}
PATCH /api/ambassadors/leads/:leadId/status
Cambiar estado del lead.
Request:
{
 "estado": "contactado"}
Response 200:
{
 "id": "LEAD-001",
 "estado": "contactado"}
POST /api/ambassadors/leads/:leadId/convert
Convertir lead en referido.
Response 201:
{
 "referidoId": "BUS-003",
 "mensaje": "Lead convertido en referido"}
DELETE /api/ambassadors/leads/:leadId
Eliminar lead.
Response 200:
{
 "mensaje": "Lead eliminado"}
Comisiones y pagos
GET /api/ambassadors/commissions
Listar comisiones generadas.
Response 200:
[
 {
   "id": "COM-001",
   "referido": "ElectroMundo",
   "concepto": "Primera venta",
   "monto": "Bs 350",
   "estado": "disponible",
   "fecha": "2026-04-28"
 }]
GET /api/ambassadors/commissions/summary
Resumen de comisiones.
Response 200:
{
 "totalGenerado": "Bs 18.500",
 "disponible": "Bs 3.200",
 "pendiente": "Bs 1.100",
 "pagado": "Bs 14.200"}
GET /api/ambassadors/commissions/:commissionId
Detalle de comisión.
Response 200:
{
 "id": "COM-001",
 "referido": "ElectroMundo",
 "concepto": "Primera venta",
 "monto": "Bs 350",
 "porcentaje": 5,
 "estado": "disponible",
 "fechaGeneracion": "2026-04-28T12:00:00Z"}
POST /api/ambassadors/commissions/:commissionId/dispute
Crear reclamo sobre una comisión.
Request:
{
 "motivo": "Monto calculado incorrectamente",
 "descripcion": "La venta fue mayor al monto registrado."}
Response 201:
{
 "id": "DSP-001",
 "estado": "pendiente_revision"}
GET /api/ambassadors/wallet
Obtener billetera del embajador.
Response 200:
{
 "saldoDisponible": "Bs 3.200",
 "saldoPendiente": "Bs 1.100",
 "totalRetirado": "Bs 14.200"}
POST /api/ambassadors/wallet/withdraw
Solicitar retiro de saldo.
Request:
{
 "monto": "Bs 1000",
 "metodoPagoId": "PAYM-001"}
Response 201:
{
 "id": "WDR-001",
 "estado": "pendiente",
 "fechaEstimada": "2026-05-07"}
GET /api/ambassadors/payouts
Listar retiros solicitados.
Response 200:
[
 {
   "id": "WDR-001",
   "monto": "Bs 1000",
   "estado": "pendiente",
   "fecha": "2026-05-03"
 }]
GET /api/ambassadors/payout-methods
Listar métodos de pago del embajador.
Response 200:
[
 {
   "id": "PAYM-001",
   "tipo": "cuenta_bancaria",
   "banco": "Banco Unión",
   "ultimos4": "1234",
   "predeterminado": true
 }]
POST /api/ambassadors/payout-methods
Agregar método de pago.
Request:
{
 "tipo": "cuenta_bancaria",
 "banco": "Banco Unión",
 "numeroCuenta": "1234567890",
 "titular": "María Rojas"}
Response 201:
{
 "id": "PAYM-002",
 "mensaje": "Método de pago agregado"}
DELETE /api/ambassadors/payout-methods/:methodId
Eliminar método de pago.
Response 200:
{
 "mensaje": "Método de pago eliminado"}
Red de embajadores
GET /api/ambassadors/network
Listar red directa del embajador.
Response 200:
[
 {
   "id": "AMB-010",
   "nombre": "Luis Vargas",
   "nivel": "Silver",
   "referidos": 18,
   "estado": "Activo"
 }]
GET /api/ambassadors/network/tree
Obtener árbol de red de embajadores.
Response 200:
{
 "id": "AMB-001",
 "nombre": "María Rojas",
 "nivel": "Gold",
 "subEmbajadores": [
   {
     "id": "AMB-010",
     "nombre": "Luis Vargas",
     "nivel": "Silver"
   }
 ]}
POST /api/ambassadors/network/invitations
Invitar nuevo embajador.
Request:
{
 "email": "nuevoembajador@example.com",
 "nombre": "Luis Vargas",
 "telefono": "+59174445566"}
Response 201:
{
 "id": "INV-AMB-001",
 "estado": "enviada"}
GET /api/ambassadors/network/invitations
Listar invitaciones enviadas.
Response 200:
[
 {
   "id": "INV-AMB-001",
   "email": "nuevoembajador@example.com",
   "estado": "pendiente"
 }]
DELETE /api/ambassadors/network/invitations/:invitationId
Cancelar invitación.
Response 200:
{
 "mensaje": "Invitación cancelada"}
GET /api/ambassadors/network/ranking
Ranking de embajadores.
Response 200:
[
 {
   "posicion": 1,
   "id": "AMB-001",
   "nombre": "María Rojas",
   "conversiones": 48,
   "comisiones": "Bs 18.500"
 }]
Chat y comunicación
GET /api/ambassadors/chats
Listar conversaciones del embajador.
Response 200:
[
 {
   "id": "CHT-AMB-001",
   "participante": "ElectroMundo",
   "ultimoMensaje": "Ya completamos el registro.",
   "mensajesSinLeer": 2
 }]
POST /api/ambassadors/chats
Crear conversación.
Request:
{
 "participanteId": "BUS-001",
 "mensajeInicial": "Hola, te ayudo con el onboarding."}
Response 201:
{
 "id": "CHT-AMB-002",
 "estado": "Abierto"}
GET /api/ambassadors/chats/:chatId/messages
Obtener mensajes de una conversación.
Response 200:
[
 {
   "id": "MSG-001",
   "remitente": "embajador",
   "contenido": "Hola, te ayudo con el onboarding.",
   "fecha": "2026-05-03T10:00:00Z"
 }]
POST /api/ambassadors/chats/:chatId/messages
Enviar mensaje.
Request:
{
 "contenido": "Perfecto, revisemos tu catálogo."}
Response 201:
{
 "id": "MSG-002",
 "estado": "enviado"}
PUT /api/ambassadors/chats/:chatId/read
Marcar conversación como leída.
Response 200:
{
 "mensaje": "Conversación marcada como leída"}
Reportes y analytics
GET /api/ambassadors/reports/performance
Obtener reporte general de desempeño.
Response 200:
{
 "periodo": "mensual",
 "clics": 1240,
 "leads": 86,
 "conversiones": 31,
 "conversionRate": 36.04,
 "comisiones": "Bs 4.800"}
GET /api/ambassadors/reports/referrals
Reporte de referidos.
Response 200:
[
 {
   "referido": "ElectroMundo",
   "tipo": "empresa",
   "estado": "activo",
   "ventasGeneradas": 42,
   "comision": "Bs 850"
 }]
GET /api/ambassadors/reports/commissions
Reporte de comisiones por período.
Response 200:
{
 "periodo": "mensual",
 "total": "Bs 4.800",
 "pendiente": "Bs 900",
 "disponible": "Bs 1.500",
 "pagado": "Bs 2.400"}
GET /api/ambassadors/reports/conversion-funnel
Obtener embudo de conversión.
Response 200:
{
 "clics": 1240,
 "leads": 86,
 "registros": 48,
 "activos": 31}
GET /api/ambassadors/reports/export
Exportar reporte del embajador.
Response 200:
{
 "downloadUrl": "https://techmarket.bo/reports/amb-001-mayo.pdf"}
Asistente IA para embajadores
POST /api/ambassadors/ai/query
Enviar consulta al asistente IA.
Request:
{
 "consulta": "¿Qué prospectos debo priorizar esta semana?"}
Response 200:
{
 "respuesta": {
   "resumen": "Prioriza empresas con alta intención y onboarding incompleto.",
   "acciones": [
     "Contactar leads con probabilidad mayor a 70%",
     "Reactivar prospectos sin respuesta en los últimos 5 días",
     "Impulsar empresas que aún no publicaron productos"
   ],
   "foco": "conversion"
 }}
GET /api/ambassadors/ai/insights
Obtener insights automáticos.
Response 200:
{
 "radar": [
   { "etiqueta": "Calidad de leads", "valor": 82 },
   { "etiqueta": "Velocidad de seguimiento", "valor": 74 },
   { "etiqueta": "Conversión", "valor": 69 },
   { "etiqueta": "Potencial de comisiones", "valor": 88 }
 ],
 "recomendacion": "Hay buen potencial en empresas de hardware y servicios técnicos."}
POST /api/ambassadors/ai/prospect-score
Calcular score de un prospecto.
Request:
{
 "leadId": "LEAD-001"}
Response 200:
{
 "leadId": "LEAD-001",
 "score": 78,
 "probabilidadCierre": "alta",
 "motivos": [
   "Tiene catálogo tecnológico",
   "Respondió en menos de 24 horas",
   "Está en ciudad con alta demanda"
 ]}
POST /api/ambassadors/ai/follow-up-suggestion
Generar sugerencia de seguimiento.
Request:
{
 "referidoId": "BUS-001"}
Response 200:
{
 "mensajeSugerido": "Hola Carlos, vi que ya completaste tu perfil. El siguiente paso ideal es publicar tus primeros 3 productos para activar visibilidad en marketplace.",
 "canalRecomendado": "whatsapp"}
POST /api/ambassadors/ai/improvement-plan
Solicitar plan de mejora.
Request:
{
 "area": "conversion"}
Response 200:
{
 "plan": {
   "objetivo": "Aumentar conversión de leads en 15%",
   "acciones": [
     "Contactar leads nuevos en menos de 2 horas",
     "Usar mensajes personalizados por tipo de negocio",
     "Agendar seguimiento a los 3 días si no completan registro"
   ],
   "tiempoEstimado": "30 días"
 }}


5. MÓDULO ESPECIALISTA/Tecnico (45+ endpoints)
Perfil, servicios y portafolio (s)
GET /api/specialists/profile
Obtener perfil del técnico.
Json
{
  "id": "TEC-01",
  "nombre": "Alejandro Torres",
  "especialidad": "Reparación de laptops",
  "ubicacion": "Santa Cruz",
  "calificacion": 4.8
}
PUT /api/specialists/profile
Actualizar perfil del técnico.
Json
{
  "mensaje": "Perfil actualizado correctamente"
}

GET /api/specialists/profile/stats
Obtener KPIs del técnico (trabajos completados, reseñas, calificación).
Json
{
  "trabajosCompletados": 286,
  "totalResenas": 112,
  "calificacionPromedio": 4.8
}

POST /api/specialists/profile/photo
Subir o cambiar foto de perfil.
Json
{
  "url": "https://cdn.techmarket.bo/specialists/tec-01-avatar.jpg"
}

GET /api/specialists/services
Listar servicios publicados por el técnico.
Json
[
  {
    "id": "s-1",
    "nombre": "Reparación de laptops",
    "descripcion": "Diagnóstico detallado, cambio de componentes y pruebas.",
    "precio": "Bs 120.000",
    "tipo": "Reparación",
    "destacado": true
  }
]

POST /api/specialists/services
Crear un nuevo servicio.
Json
{
  "id": "s-5",
  "nombre": "Armado de PC escritorio",
  "mensaje": "Servicio creado exitosamente"
}

PUT /api/specialists/services/:serviceId
Actualizar un servicio existente.
Json
{
  "mensaje": "Servicio actualizado"
}

DELETE /api/specialists/services/:serviceId
Eliminar un servicio del catálogo.
Json
{
  "mensaje": "Servicio eliminado"
}

PATCH /api/specialists/services/:serviceId/toggle-featured
Destacar o quitar destacado de un servicio.
Json
{
  "destacado": true
}

GET /api/specialists/portfolio
Listar trabajos del portafolio (casos realizados con evidencia).
Json
[
  {
    "id": "p-1",
    "titulo": "Laptop con sobrecalentamiento",
    "servicio": "Mantenimiento preventivo",
    "resultado": "Temperatura estable y mejora de rendimiento",
    "fecha": "2026-04"
  }
]

POST /api/specialists/portfolio
Agregar un trabajo al portafolio.
Json
{
  "id": "p-4",
  "mensaje": "Trabajo agregado al portafolio"
}
DELETE /api/specialists/portfolio/:itemId
Eliminar un trabajo del portafolio.
Json
{
  "mensaje": "Trabajo eliminado del portafolio"
}

Disponibilidad y agenda (s)
GET /api/specialists/availability
Obtener configuración de disponibilidad (días, horarios, modalidad).
Json
{
  "estado": "disponible",
  "dias": ["lunes", "martes", "miercoles", "jueves", "viernes", "sabado"],
  "horario": { "inicio": "08:00", "fin": "18:00" },
  "modalidad": ["presencial", "remoto", "domicilio"],
  "cobertura": "Santa Cruz de la Sierra"
}

PUT /api/specialists/availability
Actualizar configuración de disponibilidad.
Json
{
  "mensaje": "Disponibilidad actualizada"
}

PATCH /api/specialists/availability/status
Cambiar estado actual (disponible/ocupado/ausente).
Json
{
  "estado": "ocupado",
  "tiempoRespuesta": "45 min"
}

GET /api/specialists/calendar
Obtener agenda con citas y bloques de tiempo.
Json
[
  {
    "id": "cita-01",
    "cliente": "Carlos M.",
    "servicio": "Mantenimiento preventivo",
    "fecha": "2026-04-10",
    "hora": "10:00",
    "estado": "confirmada",
    "modalidad": "domicilio"
  }
]

POST /api/specialists/calendar/blocks
Bloquear horario no disponible manualmente.
Json
{
  "mensaje": "Bloque agregado a la agenda"
}

DELETE /api/specialists/calendar/blocks/:blockId
Eliminar un bloque de horario.
Json
{
  "mensaje": "Bloque eliminado"
}


Solicitudes y proyectos
GET /api/specialists/requests
Listar solicitudes de servicio recibidas.
Json
[
  {
    "id": "req-01",
    "cliente": "Laura P.",
    "servicio": "Reparación de laptops",
    "fecha": "2026-04-09",
    "estado": "pendiente",
    "urgencia": "alta"
  }
]

PATCH /api/specialists/requests/:requestId/respond
Aceptar o rechazar una solicitud.
Json
{
  "accion": "aceptada",
  "mensaje": "Solicitud aceptada, se notificó al cliente"
}

GET /api/specialists/projects
Listar proyectos activos del técnico.
Json
[
  {
    "id": "proy-01",
    "cliente": "Carlos M.",
    "servicio": "Mantenimiento preventivo",
    "estado": "en_progreso",
    "fechaAsignacion": "2026-04-08"
  }
]

GET /api/specialists/projects/:projectId
Obtener detalle de un proyecto específico.
Json
{
  "id": "proy-01",
  "cliente": { "nombre": "Carlos M.", "telefono": "+591 7XX XXX XXX" },
  "servicio": "Mantenimiento preventivo",
  "fecha": "2026-04-10",
  "estado": "en_progreso",
  "descripcion": "Limpieza interna, cambio de pasta térmica"
}

PATCH /api/specialists/projects/:projectId/status
Cambiar estado del proyecto (en_progreso, completado, cancelado).
Json
{
  "estado": "completado",
  "mensaje": "Proyecto marcado como completado"
}

GET /api/specialists/projects/history
Historial de proyectos finalizados.
Json
[
  {
    "id": "proy-00",
    "cliente": "Sofía R.",
    "servicio": "Instalación de redes",
    "fecha": "2026-03-28",
    "total": "Bs 250.000"
  }
]


Chat y archivos
GET /api/specialists/chats
Listar conversaciones activas del técnico.
Json
[
  {
    "id": "chat-01",
    "cliente": "Carlos M.",
    "ultimoMensaje": "¿A qué hora llegas?",
    "noLeidos": 2,
    "ultimaActividad": "2026-04-10T09:30:00Z"
  }
]

GET /api/specialists/chats/:chatId
Obtener mensajes de una conversación.
Json
[
  {
    "id": "msg-01",
    "remitente": "cliente",
    "contenido": "¿A qué hora llegas?",
    "fecha": "2026-04-10T09:30:00Z",
    "tipo": "texto"
  }
]

POST /api/specialists/chats/:chatId/messages
Enviar un mensaje en una conversación.
Json
{
  "id": "msg-03",
  "fecha": "2026-04-10T09:32:00Z"
}

GET /api/specialists/chats/:chatId/files
Obtener archivos compartidos en una conversación.
Json
[
  { "id": "f-1", "nombre": "diagnostico.pdf", "tamano": "2.4 MB", "fecha": "2026-04-09" }
]

POST /api/specialists/chats/:chatId/files
Subir un archivo a la conversación.
Json
{
  "id": "f-2",
  "url": "https://cdn.techmarket.bo/files/factura-01.pdf"
}

GET /api/specialists/files
Listar archivos propios del técnico (no ligados a chat).
Json
[
  { "id": "f-3", "nombre": "certificado_cisco.pdf", "tamano": "1.1 MB", "subido": "2026-03-15" }
]

POST /api/specialists/files
Subir un archivo al repositorio personal.
Json
{
  "id": "f-4",
  "mensaje": "Archivo subido correctamente"
}

DELETE /api/specialists/files/:fileId
Eliminar un archivo del repositorio.
Json
{
  "mensaje": "Archivo eliminado"
}


Pagos e ingresos
GET /api/specialists/wallet
Obtener saldo disponible y total de ingresos.
Json
{
  "saldoDisponible": "Bs 1.850.000",
  "ingresosTotales": "Bs 24.300.000",
  "enProceso": "Bs 420.000"
}

GET /api/specialists/transactions
Listar historial de transacciones (ingresos por servicios).
Json
[
  {
    "id": "tx-01",
    "servicio": "Reparación de laptops",
    "cliente": "Laura P.",
    "monto": "Bs 120.000",
    "fecha": "2026-04-08",
    "estado": "completado"
  }
]

GET /api/specialists/transactions/:transactionId
Obtener detalle de una transacción.
Json
{
  "id": "tx-01",
  "servicio": "Reparación de laptops",
  "cliente": "Laura P.",
  "monto": "Bs 120.000",
  "comisionPlataforma": "Bs 12.000",
  "neto": "Bs 108.000",
  "fecha": "2026-04-08",
  "estado": "completado"
}

POST /api/specialists/wallet/withdraw
Solicitar retiro de saldo disponible.
Json
{
  "monto": "Bs 500.000",
  "mensaje": "Solicitud de retiro enviada",
  "fechaEstimada": "2026-04-14"
}

GET /api/specialists/earnings/summary
Resumen de ingresos por período (semanal/mensual).
Json
{
  "periodo": "mensual",
  "total": "Bs 4.200.000",
  "serviciosRealizados": 12,
  "promedioPorServicio": "Bs 350.000"
}


Reseñas y certificaciones
GET /api/specialists/reviews
Listar reseñas recibidas por el técnico.
Json
[
  {
    "id": "r-1",
    "cliente": "Carlos M.",
    "estrellas": 5,
    "comentario": "Solucionó el problema en menos de un día.",
    "servicio": "Mantenimiento preventivo",
    "fecha": "2026-04-08"
  }
]

GET /api/specialists/reviews/:reviewId
Obtener detalle de una reseña específica.
Json
{
  "id": "r-1",
  "cliente": "Carlos M.",
  "estrellas": 5,
  "comentario": "Solucionó el problema en menos de un día.",
  "respuestaTecnico": null,
  "servicio": "Mantenimiento preventivo",
  "fecha": "2026-04-08"
}

POST /api/specialists/reviews/:reviewId/respond
Responder a una reseña recibida.
Json
{
  "respuesta": "Gracias, Carlos. Me alegra haber ayudado.",
  "mensaje": "Respuesta publicada"
}

GET /api/specialists/certifications
Listar certificaciones del técnico.
Json
[
  {
    "id": "cert-01",
    "nombre": "Certificación Cisco CCNA",
    "institucion": "Cisco Academy",
    "fechaObtencion": "2025-06",
    "archivoUrl": "https://cdn.techmarket.bo/certs/cisco-ccna.pdf"
  }
]

POST /api/specialists/certifications
Agregar una certificación.
Json
{
  "id": "cert-03",
  "mensaje": "Certificación agregada"
}

DELETE /api/specialists/certifications/:certId
Eliminar una certificación.
Json
{
  "mensaje": "Certificación eliminada"
}
PATCH /api/specialists/certifications/:certId/verify
Solicitar verificación de certificación por el equipo TechMarket.
Json
{
  "estado": "en_verificacion",
  "mensaje": "Certificación enviada para verificación"
}


Asistente IA para tarifas y mejoras

POST /api/specialists/ai/query
Enviar consulta al asistente IA.
Json
{
  "consulta": "¿Qué servicios debo priorizar esta semana?",
  "respuesta": {
    "resumen": "Tu mejor palanca hoy es ejecutar un triaging por urgencia y zona.",
    "planAccion": [
      "Etiqueta solicitudes en crítica, importante y seguimiento.",
      "Agrupa visitas por zona para compactar desplazamientos."
    ],
    "foco": "disponibilidad"
  }
}

GET /api/specialists/ai/insights
Obtener insights generados por IA sobre el perfil y operación.
Json
{
  "radar": [
    { "etiqueta": "Urgencia de casos", "valor": 79 },
    { "etiqueta": "Probabilidad de cierre", "valor": 72 },
    { "etiqueta": "Carga operativa", "valor": 66 },
    { "etiqueta": "Potencial de reputación", "valor": 84 }
  ],
  "recomendacion": "Momento favorable para captar y responder.",
  "focoSugerido": "Reforzar portafolio y velocidad de respuesta"
}

POST /api/specialists/ai/pricing-suggestion
Solicitar sugerencia de precios para un servicio.
Json
{
  "servicio": "Reparación de laptops",
  "precioActual": "Bs 120.000",
  "sugerencia": {
    "precioRecomendado": "Bs 135.000",
    "rangoOptimo": { "min": "Bs 110.000", "max": "Bs 150.000" },
    "justificacion": "Demanda alta en tu zona, calificación 4.8 estrellas"
  }
}

POST /api/specialists/ai/improvement-plan
Solicitar plan de mejora personalizado.
Json
{
  "area": "reputacion",
  "plan": {
    "objetivo": "Mejorar de 4.8 a 4.9 estrellas",
    "acciones": [
      "Responder reseñas en menos de 2 horas.",
      "Solicitar reseña después de cada servicio completado.",
      "Ofrecer seguimiento post-servicio."
    ],
    "tiempoEstimado": "30 días"
  }
}

POST /api/specialists/ai/schedule-optimization
Solicitar optimización de agenda y horarios.
Json
{
  "sugerencia": "Agrupar visitas por zona puede liberar 1 ventana adicional de atención.",
  "planSugerido": [
    "Confirmar agenda del día siguiente antes de las 18:00.",
    "Agrupar visitas en zonas norte, sur y centro por día."
  ]
}


6. SISTEMAS GENERALES (30+ endpoints)/
Búsqueda global
GET /api/search/global
Buscar contenido en todo TechMarket: productos, servicios, empresas, especialistas, usuarios, comunidades y publicaciones.

Response (200 OK):
json
{
  "total": 25,
  "resultados": [
    {
      "id": "PRD-001",
      "tipo": "producto",
      "titulo": "Laptop Pro 14",
      "descripcion": "Laptop para trabajo y estudio",
      "url": "/cliente/marketplace/laptop-pro-14"
    }
  ]
}

GET /api/search/suggestions
Obtener sugerencias de busqueda mientras el usuario escribe.

Response (200 OK):
json
[
  { "texto": "laptop gamer", "tipo": "termino" },
  { "texto": "Laptop Pro 14", "tipo": "producto" }
]

GET /api/search/trending
Listar busquedas populares dentro de la plataforma.

Response (200 OK):
json
[
  { "texto": "mantenimiento laptop", "busquedas": 184 },
  { "texto": "monitor ultrawide", "busquedas": 142 }
]

GET /api/search/history
Listar historial de busquedas del usuario autenticado.

Response (200 OK):
json
[
  {
    "id": "SRH-001",
    "query": "servicio tecnico laptop",
    "fecha": "2026-04-28T15:30:00Z"
  }
]

POST /api/search/history
Guardar una busqueda realizada por el usuario.

Request:
json
{
  "query": "servicio tecnico laptop",
  "tipo": "servicio"
}

Response (201 Created):
json
{ "id": "SRH-001", "mensaje": "Busqueda guardada" }

DELETE /api/search/history/:historyId
Eliminar una busqueda del historial.

Response (200 OK):
json
{ "mensaje": "Busqueda eliminada del historial" }

Notificaciones

GET /api/notifications
Listar notificaciones del usuario autenticado.

Response (200 OK):
json
[
  {
    "id": "NOT-001",
    "titulo": "Nuevo mensaje recibido",
    "mensaje": "FixCloud Soporte respondio tu consulta.",
    "leida": false,
    "fecha": "2026-04-28T15:30:00Z"
  }
]

GET /api/notifications/unread-count
Obtener la cantidad de notificaciones no leidas.

Response (200 OK):
json
{ "noLeidas": 3 }

PUT /api/notifications/:notificationId/read
Marcar una notificacion como leida.

Response (200 OK):
json
{ "id": "NOT-001", "leida": true }

PUT /api/notifications/read-all
Marcar todas las notificaciones como leidas.

Response (200 OK):
json
{ "mensaje": "Todas las notificaciones marcadas como leidas" }

DELETE /api/notifications/:notificationId
Eliminar una notificacion.

Response (200 OK):
json
{ "mensaje": "Notificacion eliminada" }

GET /api/notifications/preferences
Obtener preferencias de notificaciones del usuario.

Response (200 OK):
json
{
  "email": true,
  "push": true,
  "inApp": true
}

PUT /api/notifications/preferences
Actualizar preferencias de notificaciones.

Request:
json
{
  "email": true,
  "push": false,
  "inApp": true
}

Response (200 OK):
json
{ "mensaje": "Preferencias actualizadas" }

Mensajería unificada
GET /api/conversations
Listar conversaciones del usuario autenticado.

Response (200 OK):
json
[
  {
    "id": "CONV-001",
    "titulo": "Consulta sobre Laptop Pro 14",
    "ultimoMensaje": "Si, tenemos stock disponible.",
    "mensajesSinLeer": 1
  }
]

POST /api/conversations
Crear una nueva conversacion.

Request:
json
{
  "participanteId": "EMP-001",
  "tipo": "cliente_empresa",
  "mensajeInicial": "Hola, quiero consultar disponibilidad."
}

Response (201 Created):
json
{ "id": "CONV-001", "estado": "Abierta" }

GET /api/conversations/:conversationId/messages
Obtener mensajes de una conversacion.

Response (200 OK):
json
[
  {
    "id": "MSG-001",
    "remitenteId": "USR-001",
    "contenido": "Hola, quiero consultar disponibilidad.",
    "fecha": "2026-04-28T15:10:00Z"
  }
]

POST /api/conversations/:conversationId/messages
Enviar un mensaje dentro de una conversacion.

Request:
json
{ "contenido": "Perfecto, necesito precio final." }

Response (201 Created):
json
{
  "id": "MSG-002",
  "contenido": "Perfecto, necesito precio final.",
  "estado": "enviado"
}

PUT /api/conversations/:conversationId/read
Marcar una conversacion como leida.

Response (200 OK):
json
{ "mensaje": "Conversacion marcada como leida" }

DELETE /api/messages/:messageId
Eliminar un mensaje enviado por el usuario.

Response (200 OK):
json
{ "mensaje": "Mensaje eliminado" }

Pagos y transacciones

GET /api/payments/methods
Listar metodos de pago guardados.

Response (200 OK):
json
[
  {
    "id": "PM-001",
    "tipo": "tarjeta",
    "marca": "Visa",
    "ultimos4": "4242",
    "predeterminado": true
  }
]

POST /api/payments/methods
Agregar un metodo de pago.

Request:
json
{
  "tipo": "tarjeta",
  "token": "tok_visa_4242",
  "predeterminado": true
}

Response (201 Created):
json
{ "id": "PM-002", "mensaje": "Metodo de pago agregado" }

DELETE /api/payments/methods/:paymentMethodId
Eliminar un metodo de pago.

Response (200 OK):
json
{ "mensaje": "Metodo de pago eliminado" }

POST /api/payments/intents
Crear una intencion de pago.

Request:
json
{
  "referenciaId": "ORD-001",
  "monto": 7200,
  "moneda": "BOB",
  "metodoPagoId": "PM-001"
}

Response (201 Created):
json
{
  "paymentIntentId": "PAY-001",
  "estado": "pendiente",
  "monto": 7200
}

POST /api/payments/confirm
Confirmar un pago.

Request:
json
{ "paymentIntentId": "PAY-001" }

Response (200 OK):
json
{
  "transactionId": "TRX-001",
  "estado": "aprobado",
  "monto": 7200
}

GET /api/transactions
Listar transacciones del usuario.

Response (200 OK):
json
[
  {
    "id": "TRX-001",
    "concepto": "compra_marketplace",
    "monto": 7200,
    "moneda": "BOB",
    "estado": "aprobado"
  }
]

GET /api/transactions/:transactionId
Obtener detalle de una transaccion.

Response (200 OK):
json
{
  "id": "TRX-001",
  "monto": 7200,
  "moneda": "BOB",
  "estado": "aprobado",
  "fecha": "2026-04-28T15:42:00Z"
}

GET /api/invoices
Listar comprobantes o facturas del usuario.

Response (200 OK):
json
[
  {
    "id": "INV-001",
    "transactionId": "TRX-001",
    "numero": "TM-2026-0001",
    "monto": 7200
  }
]

GET /api/invoices/:invoiceId/download
Descargar comprobante o factura.

Response (200 OK):
json
{
  "downloadUrl": "https://techmarket.bo/invoices/inv-001.pdf"
}

POST /api/payments/refunds
Solicitar reembolso de una transaccion.

Request:
json
{
  "transactionId": "TRX-001",
  "motivo": "Producto no disponible"
}

Response (201 Created):
json
{ "refundId": "RFD-001", "estado": "pendiente_revision" }


Soporte y reportes

POST /api/support/tickets
Crear un ticket de soporte.

Request:
json
{
  "categoria": "pagos",
  "asunto": "No veo mi comprobante",
  "descripcion": "Realice el pago pero no aparece la factura."
}

Response (201 Created):
json
{ "id": "TCK-001", "estado": "abierto" }

GET /api/support/tickets
Listar tickets de soporte del usuario.

Response (200 OK):
json
[
  {
    "id": "TCK-001",
    "asunto": "No veo mi comprobante",
    "estado": "abierto",
    "fecha": "2026-04-28T16:15:00Z"
  }
]

GET /api/support/tickets/:ticketId
Obtener detalle de un ticket.

Response (200 OK):
json
{
  "id": "TCK-001",
  "asunto": "No veo mi comprobante",
  "descripcion": "Realice el pago pero no aparece la factura.",
  "estado": "abierto"
}

POST /api/support/tickets/:ticketId/messages
Enviar mensaje dentro de un ticket.

Request:
json
{ "contenido": "Adjunto captura del pago realizado." }

Response (201 Created):
json
{ "id": "TMSG-001", "mensaje": "Respuesta enviada" }

PUT /api/support/tickets/:ticketId/status
Actualizar estado de un ticket.

Request:
json
{ "estado": "cerrado" }

Response (200 OK):
json
{ "id": "TCK-001", "estado": "cerrado" }

POST /api/reports
Reportar un usuario, empresa, publicacion, producto, servicio, mensaje o comunidad.

Request:
json
{
  "tipoObjeto": "publicacion",
  "objetoId": "PUB-001",
  "motivo": "contenido_enganoso",
  "descripcion": "La publicacion tiene informacion incorrecta."
}

Response (201 Created):
json
{ "id": "REP-001", "estado": "pendiente_revision" }

GET /api/reports
Listar reportes creados por el usuario.

Response (200 OK):
json
[
  {
    "id": "REP-001",
    "tipoObjeto": "publicacion",
    "motivo": "contenido_enganoso",
    "estado": "pendiente_revision"
  }
]

GET /api/reports/:reportId
Obtener detalle de un reporte.

Response (200 OK):
json
{
  "id": "REP-001",
  "tipoObjeto": "publicacion",
  "objetoId": "PUB-001",
  "estado": "pendiente_revision"
}

Configuración (países, ciudades, monedas)
GET /api/config/countries
Listar paises disponibles.

Response (200 OK):
json
[
  {
    "codigo": "BO",
    "nombre": "Bolivia",
    "telefonoPrefijo": "+591",
    "monedaDefault": "BOB"
  }
]

GET /api/config/countries/:countryCode/cities
Listar ciudades disponibles por pais.

Response (200 OK):
json
[
  { "id": "CITY-SCZ", "nombre": "Santa Cruz" },
  { "id": "CITY-LPZ", "nombre": "La Paz" }
]

GET /api/config/currencies
Listar monedas soportadas.

Response (200 OK):
json
[
  { "codigo": "BOB", "nombre": "Boliviano", "simbolo": "Bs" },
  { "codigo": "USD", "nombre": "Dolar estadounidense", "simbolo": "$" }
]

GET /api/config/categories
Listar categorias generales de productos y servicios.

Response (200 OK):
json
[
  {
    "id": "CAT-001",
    "nombre": "Computacion",
    "subcategorias": [
      { "id": "CAT-001-1", "nombre": "Laptops" }
    ]
  }
]

GET /api/config/user-types
Listar tipos de usuario permitidos.

Response (200 OK):
json
[
  { "id": "cliente", "nombre": "Cliente" },
  { "id": "empresa", "nombre": "Empresa" },
  { "id": "especialista", "nombre": "Especialista" },
  { "id": "embajador", "nombre": "Embajador" },
  { "id": "admin", "nombre": "Administrador" }
]

GET /api/config/platform
Obtener configuracion publica de la plataforma.

Response (200 OK):
json
{
  "nombre": "TechMarket",
  "paisDefault": "BO",
  "monedaDefault": "BOB",
  "soporteEmail": "soporte@techmarket.bo"
}

GET /api/config/payment-options
Listar formas de pago disponibles.

Response (200 OK):
json
[
  { "id": "tarjeta", "nombre": "Tarjeta de debito/credito" },
  { "id": "qr", "nombre": "Pago QR" },
  { "id": "transferencia", "nombre": "Transferencia bancaria" }
]

Admin y moderación
GET /api/admin/dashboard
Obtener resumen general de la plataforma.

Response (200 OK):
json
{
  "usuarios": 1240,
  "empresas": 210,
  "reportesPendientes": 18,
  "transaccionesHoy": 37
}

GET /api/admin/users
Listar usuarios registrados con filtros administrativos.

Response (200 OK):
json
[
  {
    "id": "USR-001",
    "email": "usuario@example.com",
    "nombre": "Juan Perez",
    "tipo": "cliente",
    "estado": "Activo"
  }
]

PUT /api/admin/users/:userId/status
Actualizar estado de un usuario.

Request:
json
{
  "estado": "suspendido",
  "motivo": "Actividad sospechosa"
}

Response (200 OK):
json
{ "id": "USR-001", "estado": "suspendido" }

GET /api/admin/reports
Listar reportes pendientes de revision.

Response (200 OK):
json
[
  {
    "id": "REP-001",
    "tipoObjeto": "publicacion",
    "motivo": "contenido_enganoso",
    "estado": "pendiente_revision"
  }
]

PUT /api/admin/reports/:reportId/status
Actualizar estado de un reporte.

Request:
json
{
  "estado": "resuelto",
  "accion": "contenido_ocultado"
}

Response (200 OK):
json
{ "id": "REP-001", "estado": "resuelto" }

GET /api/admin/moderation/queue
Listar contenido pendiente de moderacion.

Response (200 OK):
json
[
  {
    "id": "MOD-001",
    "tipo": "publicacion",
    "objetoId": "PUB-001",
    "prioridad": "alta"
  }
]

POST /api/admin/moderation/actions
Ejecutar una accion de moderacion.

Request:
json
{
  "tipoObjeto": "publicacion",
  "objetoId": "PUB-001",
  "accion": "ocultar",
  "motivo": "Contenido engañoso"
}

Response (201 Created):
json
{ "id": "ACT-001", "accion": "ocultar" }

GET /api/admin/audit-logs
Consultar historial de acciones administrativas.

Response (200 OK):
json
[
  {
    "id": "AUD-001",
    "actorId": "ADM-001",
    "accion": "usuario_suspendido",
    "fecha": "2026-04-28T17:10:00Z"
  }
]

