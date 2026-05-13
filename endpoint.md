# ENDPOINTS TECHMARKET
## 1. AUTENTICACIÓN (8 endpoints)
Registro, login, logout, refresh token, recuperar contraseña, verificar email
### Perfil de usuario y configuración

#### 1. `POST /auth/register`
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

#### 2. `POST /auth/login`
Autenticar usuario y obtener token JWT.
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
#### 3. `POST /auth/logout`
Headers: Authorization: Bearer {token}

Response (200 OK):
{
  "mensaje": "Sesión cerrada exitosamente",
  "timestamp": "2026-04-28T15:30:00Z"
}

#### 4. `POST /auth/refresh-token`
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

#### 5. `POST /auth/forgot-password`
Solicitar recuperación de contraseña.

{
  "email": "usuario@example.com"
}

### Perfil de Usuario

#### 6. `GET /users/profile`
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
#### 7. `PUT /users/profile`
Actualizar perfil del usuario autenticado.
{
  "nombre": "Juan",
  "apellido": "Pérez García",
  "telefono": "+56987654321",
  "ciudad": "La Paz",
  "descripcion": "Comprador de productos tecnológicos"
}
#### 8. `GET /users/public/:userId`
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




## 2. MÓDULO CLIENTE (50+ endpoints)
### Perfil y direcciones
#### 9. `GET /api/clients/profile`
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
#### 10. `PUT /api/clients/profile`
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
#### 11. `GET /api/clients/addresses`
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
#### 12. `POST /api/clients/addresses`
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
#### 13. `PUT /api/clients/addresses/:addressId`
Actualizar una dirección existente.
Request:
json
{
 "direccion": "Av. Equipetrol, Calle 5, #125 (Nueva numeración)"
}
Response (200 OK): (Retorna la dirección actualizada)
#### 14. `DELETE /api/clients/addresses/:addressId`
Eliminar una dirección.
Response (200 OK):
json
{ "mensaje": "Dirección eliminada correctamente" }
#### 15. `PUT /api/clients/addresses/:addressId/default`
Establecer una dirección como predeterminada.
Response (200 OK):
json
{ "mensaje": "Dirección establecida como predeterminada" }

### Marketplace y búsqueda (productos, categorías, empresas)
#### 16. `GET /api/marketplace/products`
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
#### 17. `GET /api/marketplace/products/:productId`
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
#### 18. `GET /api/marketplace/categories`
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

#### 19. `GET /api/marketplace/categories/:categoryId/products`
Listar productos de una categoría específica.

Response (200 OK): (Retorna la misma estructura que GET /api/marketplace/products)
#### 20. `GET /api/marketplace/companies`
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
#### 21. `GET /api/marketplace/companies/:companyId`
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
#### 22. `GET /api/marketplace/companies/:companyId/products`
Listar el catálogo de productos de una empresa.

Response (200 OK): (Retorna estructura de paginación de productos)
### Órdenes y compras
#### 23. `GET /api/clients/cart`
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
#### 24. `POST /api/clients/cart/items`
Agregar un producto al carrito.

Request:
json
{ "productoId": "PROD-104", "cantidad": 2 }
Response (200 OK): (Retorna el carrito actualizado)
#### 25. `PUT /api/clients/cart/items/:itemId`
Actualizar la cantidad de un ítem.

Request:
json
{ "cantidad": 3 }
Response (200 OK): (Retorna el carrito actualizado)
#### 26. `DELETE /api/clients/cart/items/:itemId`
Eliminar un ítem del carrito.

Response (200 OK): (Retorna el carrito actualizado)
#### 27. `DELETE /api/clients/cart`
Vaciar el carrito completo.

Response (200 OK):
json
{ "mensaje": "Carrito vaciado correctamente" }
#### 28. `POST /api/clients/checkout`
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
#### 29. `GET /api/clients/orders`
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
#### 30. `GET /api/clients/orders/:orderId`
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
#### 31. `PUT /api/clients/orders/:orderId/cancel`
Solicitar la cancelación de una orden.

Request:
json
{ "motivo": "Me arrepentí de la compra" }
Response (200 OK):
json
{ "mensaje": "Orden cancelada", "nuevoEstado": "Cancelada" }
### Reseñas y valoraciones
#### 32. `POST /api/clients/reviews/products/:productId`
Crear una reseña de producto.

Request:
json
{ "calificacion": 5, "comentario": "Excelente equipo" }
Response (201 Created):
json
{ "id": "REV-050", "mensaje": "Reseña publicada" }
#### 33. `PUT /api/clients/reviews/:reviewId`
Editar una reseña existente.

Request:
json
{ "calificacion": 4, "comentario": "Buen equipo, pero tardó el envío" }
Response (200 OK): (Retorna la reseña actualizada)
#### 34. `DELETE /api/clients/reviews/:reviewId`
Eliminar una reseña.

Response (200 OK):
json
{ "mensaje": "Reseña eliminada" }
#### 35. `POST /api/clients/reviews/companies/:companyId`
Calificar a una empresa.

Request:
json
{ "calificacion": 5, "comentario": "Excelente atención al cliente" }
Response (201 Created): (Mismo formato que reseña de producto)
#### 36. `GET /api/marketplace/products/:productId/reviews`
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
### Chat y comunicación
#### 37. `GET /api/clients/chats`
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
#### 38. `POST /api/clients/chats`
Iniciar una nueva conversación.

Request:
json
{ "empresaId": "EMP-005", "asunto": "Consulta producto" }
Response (201 Created):
json
{ "chatId": "CHT-885", "estado": "Abierto" }
#### 39. `GET /api/clients/chats/:chatId/messages`
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
#### 40. `POST /api/clients/chats/:chatId/messages`
Enviar un mensaje.

Request:
json
{ "contenido": "Gracias por la información" }
Response (201 Created): (Retorna el objeto del mensaje enviado)
#### 41. `PUT /api/clients/chats/:chatId/read`
Marcar mensajes como leídos.

Response (200 OK):
json
{ "mensaje": "Chat marcado como leído" }
### Comunidades y favoritos
#### 42. `GET /api/clients/favorites/products`
Listar productos favoritos.

Response (200 OK): (Retorna array de productos reducidos)
#### 43. `POST /api/clients/favorites/products/:productId`
Agregar producto a favoritos.

Response (200 OK):
json
{ "mensaje": "Agregado a favoritos" }
#### 44. `DELETE /api/clients/favorites/products/:productId`
Quitar producto de favoritos.

Response (200 OK):
json
{ "mensaje": "Removido de favoritos" }
#### 45. `GET /api/clients/favorites/companies`
Listar empresas seguidas.

Response (200 OK): (Retorna array de empresas reducidas)
#### 46. `POST /api/clients/favorites/companies/:companyId`
Seguir a una empresa.

Response (200 OK):
json
{ "mensaje": "Ahora sigues a esta empresa" }
#### 47. `DELETE /api/clients/favorites/companies/:companyId`
Dejar de seguir empresa.

Response (200 OK):
json
{ "mensaje": "Dejaste de seguir a esta empresa" }
#### 48. `GET /api/clients/communities`
Listar comunidades del cliente.

Response (200 OK):
json
[
  { "id": "COM-01", "nombre": "PC Gamers Bolivia", "miembros": 1500 }
]
#### 49. `POST /api/clients/communities/:communityId/join`
Unirse a comunidad.

Response (200 OK):
json
{ "mensaje": "Te has unido a la comunidad" }
#### 50. `DELETE /api/clients/communities/:communityId/leave`
Salir de comunidad.

Response (200 OK):
json
{ "mensaje": "Has salido de la comunidad" }
#### 51. `GET /api/clients/communities/:communityId/posts`
Ver posts de una comunidad.

Response (200 OK):
json
[
  { "id": "POST-01", "autor": "Carlos", "contenido": "¿Qué tarjeta gráfica recomiendan?" }
]
### Notificaciones
#### 52. `GET /api/clients/notifications`
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
#### 53. `PUT /api/clients/notifications/:notificationId/read`
Marcar una notificación como leída.

Response (200 OK):
json
{ "id": "NOT-123", "leido": true }
#### 54. `PUT /api/clients/notifications/read-all`
Marcar todas como leídas.

Response (200 OK):
json
{ "mensaje": "Todas las notificaciones marcadas como leídas" }
#### 55. `DELETE /api/clients/notifications/:notificationId`
Eliminar una notificación.

Response (200 OK):
json
{ "mensaje": "Notificación eliminada" }
## 3. MÓDULO EMPRESA (60+ endpoints)
### Perfil y configuración
### Gestión de productos y publicaciones
### Campañas publicitarias
### Chat con clientes y leads
### Órdenes y ventas
### Analytics y reportes
### Gestión de equipo
### Integración IA para insights y recomendaciones

## 4. MÓDULO EMBAJADOR (50+ endpoints)

> Bloque implementado en `TechMarket-IA` para los endpoints 56 al 125 bajo `/api/ambassadors/**`.
> Las rutas marcadas como "Extensión" son ampliaciones implementadas para frontend y no alteran la numeración global 1-226.

### Perfil y configuración
#### 56. `GET /api/ambassadors/profile`
Obtener perfil del embajador autenticado.
Response 200:
{
 "id": "AMB-001",
 "nombre": "María",
 "apellido": "Rojas",
 "email": "embajador@example.com",
 "telefono": "+59171234567",
 "pais": "Bolivia",
 "ciudad": "Santa Cruz",
 "codigoReferido": "MARIA-GOLD",
 "nivel": "Gold",
 "fechaRegistro": "2026-04-01",
 "estado": "Activo",
 "avatar": "https://techmarket.bo/avatars/amb-001.jpg"}
#### 57. `PUT /api/ambassadors/profile`
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
#### 58. `POST /api/ambassadors/profile/photo`
Subir o cambiar foto de perfil.
Response 200:
{
 "url": "https://cdn.techmarket.bo/ambassadors/amb-001-avatar.jpg"}
#### 59. `GET /api/ambassadors/profile/stats`
Obtener métricas principales del embajador.
Los estados `activo` y `ACTIVE` se consideran negocios activos.
Response 200:
{
 "negociosReferidos": 48,
 "negociosActivos": 31,
 "conversionRate": 64.5,
 "comisionesTotales": "Bs 18.500",
 "nivel": "Gold"}
##### Extensión: `GET /api/ambassadors/dashboard`
Obtener un resumen consolidado para el dashboard del embajador y evitar llamadas separadas.
Response 200:
{
 "profile": {
   "id": "AMB-001",
   "nombre": "María Rojas",
   "apellido": "Rojas",
   "email": "embajador@example.com",
   "telefono": "+59171234567",
   "pais": "Bolivia",
   "ciudad": "Santa Cruz",
   "codigoReferido": "MARIA-GOLD",
   "nivel": "Gold",
   "fechaRegistro": "2026-04-01",
   "estado": "Activo",
   "avatar": "https://techmarket.bo/avatars/amb-001.jpg"
 },
 "stats": {
   "negociosReferidos": 48,
   "negociosActivos": 31,
   "conversionRate": 64.5,
   "comisionesTotales": "Bs 18.500",
   "nivel": "Gold"
 },
 "recentReferrals": [],
 "recentCommissions": [],
 "pendingActions": []}
#### 60. `GET /api/ambassadors/settings`
Obtener configuración del embajador.
Response 200:
{
 "notificacionesEmail": true,
 "notificacionesPush": true,
 "mostrarPerfilPublico": true,
 "idioma": "es"}
#### 61. `PUT /api/ambassadors/settings`
Actualizar configuración del embajador.
Request:
{
 "notificacionesEmail": true,
 "notificacionesPush": false,
 "mostrarPerfilPublico": true}
Response 200:
{
 "mensaje": "Configuración actualizada"}
### Referral links y códigos
#### 62. `GET /api/ambassadors/referral-links`
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
#### 63. `POST /api/ambassadors/referral-links`
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
#### 64. `GET /api/ambassadors/referral-links/:linkId`
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
##### Extensión: `GET /api/ambassadors/referral-links/:linkId/stats`
Obtener métricas resumidas de un link de referido.
Response 200:
{
 "linkId": "REFLINK-001",
 "codigo": "MARIA-SCZ",
 "clicks": 320,
 "registros": 24,
 "conversionRate": 7.5,
 "comisionesGeneradas": "Bs 23110"}
#### 65. `PUT /api/ambassadors/referral-links/:linkId`
Actualizar un link de referido.
Request:
{
 "nombre": "Campaña empresas premium Santa Cruz",
 "segmento": "empresas"}
Response 200:
{
 "mensaje": "Link actualizado correctamente"}
#### 66. `PATCH /api/ambassadors/referral-links/:linkId/status`
Activar o desactivar link.
Request:
{
 "activo": false}
Response 200:
{
 "id": "REFLINK-001",
 "activo": false}
#### 67. `DELETE /api/ambassadors/referral-links/:linkId`
Eliminar link de referido.
Response 200:
{
 "mensaje": "Link de referido eliminado"}
#### 68. `GET /api/ambassadors/referral-links/:linkId/qr`
Generar QR del link de referido.
Response 200:
{
 "qrUrl": "https://cdn.techmarket.bo/qr/reflink-001.png"}
#### 69. `GET /api/ambassadors/referral-codes`
Listar códigos activos del embajador.
Response 200:
[
 {
   "codigo": "MARIA-GOLD",
   "tipo": "general",
   "usos": 58,
   "activo": true
 }]
### Negocios referidos
#### 70. `GET /api/ambassadors/referrals`
Listar negocios o usuarios referidos.
El campo `nombre` corresponde al nombre del negocio referido (`ambassador_referrals.name` o el tenant asociado), no al identificador `BUS-*`.
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
##### Extensión: `GET /api/ambassadors/referrals/metrics`
Obtener métricas agregadas para la pantalla de negocios referidos.
Response 200:
{
 "totalReferidos": 9,
 "activos": 9,
 "pendientes": 0,
 "conversionRate": 100,
 "comisionTotal": "Bs 23110",
 "porMes": []}
#### 71. `POST /api/ambassadors/referrals`
Registrar manualmente un prospecto referido.
Request:
{
 "nombre": "FixCloud Bolivia",
 "tipo": "empresa",
 "contacto": "Carlos Méndez",
 "telefono": "+59170001122",
 "email": "contacto@fixcloud.bo",
 "ciudad": "Santa Cruz",
 "pais": "Bolivia"}
Response 201:
{
 "id": "BUS-002",
 "estado": "prospecto",
 "mensaje": "Prospecto registrado correctamente"}
#### 72. `GET /api/ambassadors/referrals/:referralId`
Obtener detalle de un referido.
Response 200:
{
 "id": "BUS-001",
 "nombre": "ElectroMundo",
 "estado": "ACTIVE",
 "pais": "Bolivia",
 "ciudad": "Santa Cruz",
 "categoria": "Retail",
 "fechaRegistro": "2026-04-10",
 "ultimaActividad": "2026-05-01",
 "ventasTotales": 0,
 "comisionGenerada": 850,
 "plan": "Premium",
 "contacto": {
   "nombre": "Carlos Méndez",
   "email": "contacto@electromundo.bo",
   "telefono": "+59170001122"
 }}
#### 73. `PUT /api/ambassadors/referrals/:referralId`
Actualizar datos de un referido.
Request:
{
 "contacto": "Carlos A. Méndez",
 "telefono": "+59171112233",
 "ciudad": "La Paz"}
Response 200:
{
 "mensaje": "Referido actualizado correctamente"}
#### 74. `PATCH /api/ambassadors/referrals/:referralId/status`
Actualizar estado del referido.
Request:
{
 "estado": "en_onboarding"}
Response 200:
{
 "id": "BUS-001",
 "estado": "en_onboarding"}
#### 75. `DELETE /api/ambassadors/referrals/:referralId`
Eliminar referido prospecto.
Response 200:
{
 "mensaje": "Referido eliminado correctamente"}
#### 76. `GET /api/ambassadors/referrals/:referralId/activity`
Obtener actividad del referido.
Response 200:
[
 {
   "id": "ACT-001",
   "tipo": "registro",
   "descripcion": "Empresa completó registro inicial",
   "fecha": "2026-04-10T10:00:00Z"
 }]
#### 77. `POST /api/ambassadors/referrals/:referralId/notes`
Agregar nota interna al referido.
Request:
{
 "nota": "Interesado en vender accesorios gaming."}
Response 201:
{
 "id": "NOTE-001",
 "mensaje": "Nota agregada"}
#### 78. `GET /api/ambassadors/referrals/:referralId/notes`
Listar notas del referido.
Response 200:
[
 {
   "id": "NOTE-001",
   "nota": "Interesado en vender accesorios gaming.",
   "fecha": "2026-04-29T15:00:00Z"
 }]
#### 79. `POST /api/ambassadors/referrals/:referralId/files`
Subir archivo asociado al referido.
Response 201:
{
 "id": "FILE-001",
 "url": "https://cdn.techmarket.bo/referrals/file-001.pdf"}
### Seguimiento de onboarding
#### 80. `GET /api/ambassadors/onboarding`
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
#### 81. `GET /api/ambassadors/onboarding/:onboardingId`
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
##### Extensión: `PATCH /api/ambassadors/onboarding/:businessId`
Actualizar etapa o agregar nota de seguimiento al onboarding de un negocio referido. `businessId` acepta `BUS-*` u `ONB-*`.
Request:
{
 "etapa": "en_proceso",
 "nota": "Se acordó completar catálogo esta semana"}
Response 200:
{
 "id": "ONB-001",
 "referidoId": "BUS-001",
 "nombre": "ElectroMundo",
 "progreso": 75,
 "estado": "en_proceso"}
#### 82. `POST /api/ambassadors/onboarding/:onboardingId/tasks`
Crear tarea de seguimiento.
Request:
{
 "titulo": "Ayudar a publicar primer producto",
 "fechaLimite": "2026-05-10"}
Response 201:
{
 "id": "TASK-001",
 "mensaje": "Tarea creada"}
#### 83. `GET /api/ambassadors/onboarding/:onboardingId/tasks`
Listar tareas de onboarding.
Response 200:
[
 {
   "id": "TASK-001",
   "titulo": "Ayudar a publicar primer producto",
   "estado": "pendiente",
   "fechaLimite": "2026-05-10"
 }]
#### 84. `PATCH /api/ambassadors/onboarding/tasks/:taskId/status`
Actualizar estado de una tarea.
Request:
{
 "estado": "completada"}
Response 200:
{
 "id": "TASK-001",
 "estado": "completada"}
##### Extensión: `PATCH /api/ambassadors/onboarding/:businessId/tasks/:taskId`
Actualizar título, estado, fecha límite o nota de una tarea de onboarding. `businessId` acepta `BUS-*` u `ONB-*`.
Request:
{
 "titulo": "Publicar primeros productos",
 "estado": "completada",
 "fechaLimite": "2026-05-10",
 "nota": "Tarea completada durante la llamada"}
Response 200:
{
 "id": "TASK-001",
 "estado": "completada"}
#### 85. `POST /api/ambassadors/onboarding/:onboardingId/reminders`
Crear recordatorio de seguimiento.
Request:
{
 "fecha": "2026-05-08T09:00:00Z",
 "mensaje": "Contactar para revisar avance del catálogo"}
Response 201:
{
 "id": "REM-001",
 "mensaje": "Recordatorio creado"}
#### 86. `GET /api/ambassadors/onboarding/milestones`
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
#### 87. `PATCH /api/ambassadors/onboarding/:onboardingId/milestones/:milestoneId`
Marcar hito como completado.
Response 200:
{
 "mensaje": "Hito marcado como completado"}
### Prospectos y leads
#### 88. `GET /api/ambassadors/leads`
Listar leads capturados.
Response 200:
[
 {
   "id": "LEAD-001",
   "nombre": "TecnoStore Bolivia",
   "tipo": "empresa",
   "estado": "nuevo",
   "fuente": "evento",
   "ciudad": "Santa Cruz",
   "pais": "Bolivia",
   "contacto": "Ana López",
   "telefono": "+59175556677",
   "email": "ana@tecnostore.bo",
   "notas": "Interesada en plan premium",
   "proximaAccion": "Enviar propuesta comercial",
   "historialAcciones": [],
   "fechaUltimoContacto": "2026-05-12T15:00:00Z"
 }]
#### 89. `POST /api/ambassadors/leads`
Crear nuevo lead.
Request:
{
 "nombre": "TecnoStore Bolivia",
 "tipo": "empresa",
 "contacto": "Ana López",
 "telefono": "+59175556677",
 "email": "ana@tecnostore.bo",
 "ciudad": "Santa Cruz",
 "pais": "Bolivia",
 "notas": "Interesada en plan premium",
 "proximaAccion": "Enviar propuesta comercial",
 "fuente": "evento"}
Response 201:
{
 "id": "LEAD-001",
 "mensaje": "Lead creado correctamente"}
#### 90. `GET /api/ambassadors/leads/:leadId`
Obtener detalle de un lead.
Response 200:
{
 "id": "LEAD-001",
 "nombre": "TecnoStore Bolivia",
 "tipo": "empresa",
 "estado": "nuevo",
 "probabilidadCierre": 68,
 "ciudad": "Santa Cruz",
 "pais": "Bolivia",
 "contacto": "Ana López",
 "telefono": "+59175556677",
 "email": "ana@tecnostore.bo",
 "notas": "Interesada en plan premium",
 "proximaAccion": "Enviar propuesta comercial",
 "historialAcciones": [],
 "fechaUltimoContacto": "2026-05-12T15:00:00Z"}
#### 91. `PUT /api/ambassadors/leads/:leadId`
Actualizar lead.
Request:
{
 "nombre": "TecnoStore Bolivia",
 "tipo": "empresa",
 "contacto": "Ana López",
 "telefono": "+59175556677",
 "email": "ana@tecnostore.bo",
 "ciudad": "Santa Cruz",
 "pais": "Bolivia",
 "notas": "Pidió información de planes",
 "proximaAccion": "Agendar demo",
 "fuente": "evento",
 "estado": "contactado",
 "probabilidad": 75}
Response 200:
{
 "mensaje": "Lead actualizado"}
##### Extensión: `PATCH /api/ambassadors/leads/:leadId`
Actualizar parcialmente estado, contacto, notas, probabilidad, ciudad, país, teléfono, email, fuente o fecha de último contacto.
Request:
{
 "estado": "contactado",
 "contacto": "Ana López",
 "telefono": "+59175556677",
 "email": "ana@tecnostore.bo",
 "ciudad": "Santa Cruz",
 "pais": "Bolivia",
 "notas": "Pidió información de planes",
 "proximaAccion": "Agendar demo",
 "probabilidad": 75,
 "fechaUltimoContacto": "2026-05-12T15:00:00Z"}
Response 200:
{
 "id": "LEAD-001",
 "nombre": "TecnoStore Bolivia",
 "tipo": "empresa",
 "estado": "contactado",
 "probabilidadCierre": 75,
 "ciudad": "Santa Cruz",
 "pais": "Bolivia",
 "contacto": "Ana López",
 "telefono": "+59175556677",
 "email": "ana@tecnostore.bo",
 "notas": "Pidió información de planes",
 "proximaAccion": "Agendar demo",
 "historialAcciones": [],
 "fechaUltimoContacto": "2026-05-12T15:00:00Z"}
#### 92. `PATCH /api/ambassadors/leads/:leadId/status`
Cambiar estado del lead.
Request:
{
 "estado": "contactado"}
Response 200:
{
 "id": "LEAD-001",
 "estado": "contactado"}
##### Extensión: `POST /api/ambassadors/leads/:leadId/activities`
Registrar acción o seguimiento de un lead.
Request:
{
 "tipo": "CALL",
 "nota": "Se llamó al prospecto, pidió información de planes",
 "fecha": "2026-05-12T15:00:00Z"}
Response 201:
{
 "id": "ACT-001",
 "tipo": "CALL",
 "nota": "Se llamó al prospecto, pidió información de planes",
 "fecha": "2026-05-12T15:00:00Z"}
#### 93. `POST /api/ambassadors/leads/:leadId/convert`
Convertir lead en referido.
Response 201:
{
 "referidoId": "BUS-003",
 "mensaje": "Lead convertido en referido"}
#### 94. `DELETE /api/ambassadors/leads/:leadId`
Eliminar lead.
Response 200:
{
 "mensaje": "Lead eliminado"}
### Comisiones y pagos
#### 95. `GET /api/ambassadors/commissions`
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
#### 96. `GET /api/ambassadors/commissions/summary`
Resumen de comisiones.
Response 200:
{
 "totalGenerado": "Bs 18.500",
 "disponible": "Bs 3.200",
 "pendiente": "Bs 1.100",
 "pagado": "Bs 14.200"}
#### 97. `GET /api/ambassadors/commissions/:commissionId`
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
#### 98. `POST /api/ambassadors/commissions/:commissionId/dispute`
Crear reclamo sobre una comisión.
Request:
{
 "motivo": "Monto calculado incorrectamente",
 "descripcion": "La venta fue mayor al monto registrado."}
Response 201:
{
 "id": "DSP-001",
 "estado": "pendiente_revision"}
#### 99. `GET /api/ambassadors/wallet`
Obtener billetera del embajador.
Response 200:
{
 "saldoDisponible": "Bs 3.200",
 "saldoPendiente": "Bs 1.100",
 "totalRetirado": "Bs 14.200"}
#### 100. `POST /api/ambassadors/wallet/withdraw`
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
#### 101. `GET /api/ambassadors/payouts`
Listar retiros solicitados.
Response 200:
[
 {
   "id": "WDR-001",
   "monto": "Bs 1000",
   "estado": "pendiente",
   "fecha": "2026-05-03"
 }]
#### 102. `GET /api/ambassadors/payout-methods`
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
#### 103. `POST /api/ambassadors/payout-methods`
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
#### 104. `DELETE /api/ambassadors/payout-methods/:methodId`
Eliminar método de pago.
Response 200:
{
 "mensaje": "Método de pago eliminado"}
Red de embajadores
#### 105. `GET /api/ambassadors/network`
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
#### 106. `GET /api/ambassadors/network/tree`
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
#### 107. `POST /api/ambassadors/network/invitations`
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
#### 108. `GET /api/ambassadors/network/invitations`
Listar invitaciones enviadas.
Response 200:
[
 {
   "id": "INV-AMB-001",
   "email": "nuevoembajador@example.com",
   "estado": "pendiente"
 }]
#### 109. `DELETE /api/ambassadors/network/invitations/:invitationId`
Cancelar invitación.
Response 200:
{
 "mensaje": "Invitación cancelada"}
#### 110. `GET /api/ambassadors/network/ranking`
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
### Chat y comunicación
#### 111. `GET /api/ambassadors/chats`
Listar conversaciones del embajador.
Response 200:
[
 {
   "id": "CHT-AMB-001",
   "participante": "ElectroMundo",
   "ultimoMensaje": "Ya completamos el registro.",
   "mensajesSinLeer": 2
 }]
#### 112. `POST /api/ambassadors/chats`
Crear conversación.
Request:
{
 "participanteId": "BUS-001",
 "mensajeInicial": "Hola, te ayudo con el onboarding."}
Response 201:
{
 "id": "CHT-AMB-002",
 "estado": "Abierto"}
#### 113. `GET /api/ambassadors/chats/:chatId/messages`
Obtener mensajes de una conversación.
Response 200:
[
 {
   "id": "MSG-001",
   "remitente": "embajador",
   "contenido": "Hola, te ayudo con el onboarding.",
   "fecha": "2026-05-03T10:00:00Z"
 }]
#### 114. `POST /api/ambassadors/chats/:chatId/messages`
Enviar mensaje.
Request:
{
 "contenido": "Perfecto, revisemos tu catálogo."}
Response 201:
{
 "id": "MSG-002",
 "estado": "enviado"}
#### 115. `PUT /api/ambassadors/chats/:chatId/read`
Marcar conversación como leída.
Response 200:
{
 "mensaje": "Conversación marcada como leída"}
Reportes y analytics
#### 116. `GET /api/ambassadors/reports/performance`
Obtener reporte general de desempeño.
Response 200:
{
 "periodo": "mensual",
 "clics": 1240,
 "leads": 86,
 "conversiones": 31,
 "conversionRate": 36.04,
 "comisiones": "Bs 4.800"}
#### 117. `GET /api/ambassadors/reports/referrals`
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
#### 118. `GET /api/ambassadors/reports/commissions`
Reporte de comisiones por período.
Response 200:
{
 "periodo": "mensual",
 "total": "Bs 4.800",
 "pendiente": "Bs 900",
 "disponible": "Bs 1.500",
 "pagado": "Bs 2.400"}
#### 119. `GET /api/ambassadors/reports/conversion-funnel`
Obtener embudo de conversión.
Response 200:
{
 "clics": 1240,
 "leads": 86,
 "registros": 48,
 "activos": 31}
#### 120. `GET /api/ambassadors/reports/export`
Exportar reporte del embajador.
Response 200:
{
 "downloadUrl": "https://techmarket.bo/reports/amb-001-mayo.pdf"}
Asistente IA para embajadores
#### 121. `POST /api/ambassadors/ai/query`
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
#### 122. `GET /api/ambassadors/ai/insights`
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
#### 123. `POST /api/ambassadors/ai/prospect-score`
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
#### 124. `POST /api/ambassadors/ai/follow-up-suggestion`
Generar sugerencia de seguimiento.
Request:
{
 "referidoId": "BUS-001"}
Response 200:
{
 "mensajeSugerido": "Hola Carlos, vi que ya completaste tu perfil. El siguiente paso ideal es publicar tus primeros 3 productos para activar visibilidad en marketplace.",
 "canalRecomendado": "whatsapp"}
#### 125. `POST /api/ambassadors/ai/improvement-plan`
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


## 5. MÓDULO ESPECIALISTA/Tecnico (45+ endpoints)
### Perfil, servicios y portafolio (s)
#### 126. `GET /api/specialists/profile`
Obtener perfil del técnico.
Json
{
  "id": "TEC-01",
  "nombre": "Alejandro Torres",
  "especialidad": "Reparación de laptops",
  "ubicacion": "Santa Cruz",
  "calificacion": 4.8
}
#### 127. `PUT /api/specialists/profile`
Actualizar perfil del técnico.
Json
{
  "mensaje": "Perfil actualizado correctamente"
}

#### 128. `GET /api/specialists/profile/stats`
Obtener KPIs del técnico (trabajos completados, reseñas, calificación).
Json
{
  "trabajosCompletados": 286,
  "totalResenas": 112,
  "calificacionPromedio": 4.8
}

#### 129. `POST /api/specialists/profile/photo`
Subir o cambiar foto de perfil.
Json
{
  "url": "https://cdn.techmarket.bo/specialists/tec-01-avatar.jpg"
}

#### 130. `GET /api/specialists/services`
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

#### 131. `POST /api/specialists/services`
Crear un nuevo servicio.
Json
{
  "id": "s-5",
  "nombre": "Armado de PC escritorio",
  "mensaje": "Servicio creado exitosamente"
}

#### 132. `PUT /api/specialists/services/:serviceId`
Actualizar un servicio existente.
Json
{
  "mensaje": "Servicio actualizado"
}

#### 133. `DELETE /api/specialists/services/:serviceId`
Eliminar un servicio del catálogo.
Json
{
  "mensaje": "Servicio eliminado"
}

#### 134. `PATCH /api/specialists/services/:serviceId/toggle-featured`
Destacar o quitar destacado de un servicio.
Json
{
  "destacado": true
}

#### 135. `GET /api/specialists/portfolio`
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

#### 136. `POST /api/specialists/portfolio`
Agregar un trabajo al portafolio.
Json
{
  "id": "p-4",
  "mensaje": "Trabajo agregado al portafolio"
}
#### 137. `DELETE /api/specialists/portfolio/:itemId`
Eliminar un trabajo del portafolio.
Json
{
  "mensaje": "Trabajo eliminado del portafolio"
}

Disponibilidad y agenda (s)
#### 138. `GET /api/specialists/availability`
Obtener configuración de disponibilidad (días, horarios, modalidad).
Json
{
  "estado": "disponible",
  "dias": ["lunes", "martes", "miercoles", "jueves", "viernes", "sabado"],
  "horario": { "inicio": "08:00", "fin": "18:00" },
  "modalidad": ["presencial", "remoto", "domicilio"],
  "cobertura": "Santa Cruz de la Sierra"
}

#### 139. `PUT /api/specialists/availability`
Actualizar configuración de disponibilidad.
Json
{
  "mensaje": "Disponibilidad actualizada"
}

#### 140. `PATCH /api/specialists/availability/status`
Cambiar estado actual (disponible/ocupado/ausente).
Json
{
  "estado": "ocupado",
  "tiempoRespuesta": "45 min"
}

#### 141. `GET /api/specialists/calendar`
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

#### 142. `POST /api/specialists/calendar/blocks`
Bloquear horario no disponible manualmente.
Json
{
  "mensaje": "Bloque agregado a la agenda"
}

#### 143. `DELETE /api/specialists/calendar/blocks/:blockId`
Eliminar un bloque de horario.
Json
{
  "mensaje": "Bloque eliminado"
}


Solicitudes y proyectos
#### 144. `GET /api/specialists/requests`
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

#### 145. `PATCH /api/specialists/requests/:requestId/respond`
Aceptar o rechazar una solicitud.
Json
{
  "accion": "aceptada",
  "mensaje": "Solicitud aceptada, se notificó al cliente"
}

#### 146. `GET /api/specialists/projects`
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

#### 147. `GET /api/specialists/projects/:projectId`
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

#### 148. `PATCH /api/specialists/projects/:projectId/status`
Cambiar estado del proyecto (en_progreso, completado, cancelado).
Json
{
  "estado": "completado",
  "mensaje": "Proyecto marcado como completado"
}

#### 149. `GET /api/specialists/projects/history`
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


### Chat y archivos
#### 150. `GET /api/specialists/chats`
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

#### 151. `GET /api/specialists/chats/:chatId`
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

#### 152. `POST /api/specialists/chats/:chatId/messages`
Enviar un mensaje en una conversación.
Json
{
  "id": "msg-03",
  "fecha": "2026-04-10T09:32:00Z"
}

#### 153. `GET /api/specialists/chats/:chatId/files`
Obtener archivos compartidos en una conversación.
Json
[
  { "id": "f-1", "nombre": "diagnostico.pdf", "tamano": "2.4 MB", "fecha": "2026-04-09" }
]

#### 154. `POST /api/specialists/chats/:chatId/files`
Subir un archivo a la conversación.
Json
{
  "id": "f-2",
  "url": "https://cdn.techmarket.bo/files/factura-01.pdf"
}

#### 155. `GET /api/specialists/files`
Listar archivos propios del técnico (no ligados a chat).
Json
[
  { "id": "f-3", "nombre": "certificado_cisco.pdf", "tamano": "1.1 MB", "subido": "2026-03-15" }
]

#### 156. `POST /api/specialists/files`
Subir un archivo al repositorio personal.
Json
{
  "id": "f-4",
  "mensaje": "Archivo subido correctamente"
}

#### 157. `DELETE /api/specialists/files/:fileId`
Eliminar un archivo del repositorio.
Json
{
  "mensaje": "Archivo eliminado"
}


Pagos e ingresos
#### 158. `GET /api/specialists/wallet`
Obtener saldo disponible y total de ingresos.
Json
{
  "saldoDisponible": "Bs 1.850.000",
  "ingresosTotales": "Bs 24.300.000",
  "enProceso": "Bs 420.000"
}

#### 159. `GET /api/specialists/transactions`
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

#### 160. `GET /api/specialists/transactions/:transactionId`
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

#### 161. `POST /api/specialists/wallet/withdraw`
Solicitar retiro de saldo disponible.
Json
{
  "monto": "Bs 500.000",
  "mensaje": "Solicitud de retiro enviada",
  "fechaEstimada": "2026-04-14"
}

#### 162. `GET /api/specialists/earnings/summary`
Resumen de ingresos por período (semanal/mensual).
Json
{
  "periodo": "mensual",
  "total": "Bs 4.200.000",
  "serviciosRealizados": 12,
  "promedioPorServicio": "Bs 350.000"
}


### Reseñas y certificaciones
#### 163. `GET /api/specialists/reviews`
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

#### 164. `GET /api/specialists/reviews/:reviewId`
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

#### 165. `POST /api/specialists/reviews/:reviewId/respond`
Responder a una reseña recibida.
Json
{
  "respuesta": "Gracias, Carlos. Me alegra haber ayudado.",
  "mensaje": "Respuesta publicada"
}

#### 166. `GET /api/specialists/certifications`
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

#### 167. `POST /api/specialists/certifications`
Agregar una certificación.
Json
{
  "id": "cert-03",
  "mensaje": "Certificación agregada"
}

#### 168. `DELETE /api/specialists/certifications/:certId`
Eliminar una certificación.
Json
{
  "mensaje": "Certificación eliminada"
}
#### 169. `PATCH /api/specialists/certifications/:certId/verify`
Solicitar verificación de certificación por el equipo TechMarket.
Json
{
  "estado": "en_verificacion",
  "mensaje": "Certificación enviada para verificación"
}


Asistente IA para tarifas y mejoras

#### 170. `POST /api/specialists/ai/query`
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

#### 171. `GET /api/specialists/ai/insights`
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

#### 172. `POST /api/specialists/ai/pricing-suggestion`
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

#### 173. `POST /api/specialists/ai/improvement-plan`
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

#### 174. `POST /api/specialists/ai/schedule-optimization`
Solicitar optimización de agenda y horarios.
Json
{
  "sugerencia": "Agrupar visitas por zona puede liberar 1 ventana adicional de atención.",
  "planSugerido": [
    "Confirmar agenda del día siguiente antes de las 18:00.",
    "Agrupar visitas en zonas norte, sur y centro por día."
  ]
}


## 6. SISTEMAS GENERALES (30+ endpoints)
Búsqueda global
#### 175. `GET /api/search/global`
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

#### 176. `GET /api/search/suggestions`
Obtener sugerencias de busqueda mientras el usuario escribe.

Response (200 OK):
json
[
  { "texto": "laptop gamer", "tipo": "termino" },
  { "texto": "Laptop Pro 14", "tipo": "producto" }
]

#### 177. `GET /api/search/trending`
Listar busquedas populares dentro de la plataforma.

Response (200 OK):
json
[
  { "texto": "mantenimiento laptop", "busquedas": 184 },
  { "texto": "monitor ultrawide", "busquedas": 142 }
]

#### 178. `GET /api/search/history`
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

#### 179. `POST /api/search/history`
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

#### 180. `DELETE /api/search/history/:historyId`
Eliminar una busqueda del historial.

Response (200 OK):
json
{ "mensaje": "Busqueda eliminada del historial" }

### Notificaciones

#### 181. `GET /api/notifications`
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

#### 182. `GET /api/notifications/unread-count`
Obtener la cantidad de notificaciones no leidas.

Response (200 OK):
json
{ "noLeidas": 3 }

#### 183. `PUT /api/notifications/:notificationId/read`
Marcar una notificacion como leida.

Response (200 OK):
json
{ "id": "NOT-001", "leida": true }

#### 184. `PUT /api/notifications/read-all`
Marcar todas las notificaciones como leidas.

Response (200 OK):
json
{ "mensaje": "Todas las notificaciones marcadas como leidas" }

#### 185. `DELETE /api/notifications/:notificationId`
Eliminar una notificacion.

Response (200 OK):
json
{ "mensaje": "Notificacion eliminada" }

#### 186. `GET /api/notifications/preferences`
Obtener preferencias de notificaciones del usuario.

Response (200 OK):
json
{
  "email": true,
  "push": true,
  "inApp": true
}

#### 187. `PUT /api/notifications/preferences`
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
#### 188. `GET /api/conversations`
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

#### 189. `POST /api/conversations`
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

#### 190. `GET /api/conversations/:conversationId/messages`
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

#### 191. `POST /api/conversations/:conversationId/messages`
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

#### 192. `PUT /api/conversations/:conversationId/read`
Marcar una conversacion como leida.

Response (200 OK):
json
{ "mensaje": "Conversacion marcada como leida" }

#### 193. `DELETE /api/messages/:messageId`
Eliminar un mensaje enviado por el usuario.

Response (200 OK):
json
{ "mensaje": "Mensaje eliminado" }

Pagos y transacciones

#### 194. `GET /api/payments/methods`
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

#### 195. `POST /api/payments/methods`
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

#### 196. `DELETE /api/payments/methods/:paymentMethodId`
Eliminar un metodo de pago.

Response (200 OK):
json
{ "mensaje": "Metodo de pago eliminado" }

#### 197. `POST /api/payments/intents`
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

#### 198. `POST /api/payments/confirm`
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

#### 199. `GET /api/transactions`
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

#### 200. `GET /api/transactions/:transactionId`
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

#### 201. `GET /api/invoices`
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

#### 202. `GET /api/invoices/:invoiceId/download`
Descargar comprobante o factura.

Response (200 OK):
json
{
  "downloadUrl": "https://techmarket.bo/invoices/inv-001.pdf"
}

#### 203. `POST /api/payments/refunds`
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

#### 204. `POST /api/support/tickets`
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

#### 205. `GET /api/support/tickets`
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

#### 206. `GET /api/support/tickets/:ticketId`
Obtener detalle de un ticket.

Response (200 OK):
json
{
  "id": "TCK-001",
  "asunto": "No veo mi comprobante",
  "descripcion": "Realice el pago pero no aparece la factura.",
  "estado": "abierto"
}

#### 207. `POST /api/support/tickets/:ticketId/messages`
Enviar mensaje dentro de un ticket.

Request:
json
{ "contenido": "Adjunto captura del pago realizado." }

Response (201 Created):
json
{ "id": "TMSG-001", "mensaje": "Respuesta enviada" }

#### 208. `PUT /api/support/tickets/:ticketId/status`
Actualizar estado de un ticket.

Request:
json
{ "estado": "cerrado" }

Response (200 OK):
json
{ "id": "TCK-001", "estado": "cerrado" }

#### 209. `POST /api/reports`
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

#### 210. `GET /api/reports`
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

#### 211. `GET /api/reports/:reportId`
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
#### 212. `GET /api/config/countries`
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

#### 213. `GET /api/config/countries/:countryCode/cities`
Listar ciudades disponibles por pais.

Response (200 OK):
json
[
  { "id": "CITY-SCZ", "nombre": "Santa Cruz" },
  { "id": "CITY-LPZ", "nombre": "La Paz" }
]

#### 214. `GET /api/config/currencies`
Listar monedas soportadas.

Response (200 OK):
json
[
  { "codigo": "BOB", "nombre": "Boliviano", "simbolo": "Bs" },
  { "codigo": "USD", "nombre": "Dolar estadounidense", "simbolo": "$" }
]

#### 215. `GET /api/config/categories`
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

#### 216. `GET /api/config/user-types`
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

#### 217. `GET /api/config/platform`
Obtener configuracion publica de la plataforma.

Response (200 OK):
json
{
  "nombre": "TechMarket",
  "paisDefault": "BO",
  "monedaDefault": "BOB",
  "soporteEmail": "soporte@techmarket.bo"
}

#### 218. `GET /api/config/payment-options`
Listar formas de pago disponibles.

Response (200 OK):
json
[
  { "id": "tarjeta", "nombre": "Tarjeta de debito/credito" },
  { "id": "qr", "nombre": "Pago QR" },
  { "id": "transferencia", "nombre": "Transferencia bancaria" }
]

Admin y moderación
#### 219. `GET /api/admin/dashboard`
Obtener resumen general de la plataforma.

Response (200 OK):
json
{
  "usuarios": 1240,
  "empresas": 210,
  "reportesPendientes": 18,
  "transaccionesHoy": 37
}

#### 220. `GET /api/admin/users`
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

#### 221. `PUT /api/admin/users/:userId/status`
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

#### 222. `GET /api/admin/reports`
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

#### 223. `PUT /api/admin/reports/:reportId/status`
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

#### 224. `GET /api/admin/moderation/queue`
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

#### 225. `POST /api/admin/moderation/actions`
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

#### 226. `GET /api/admin/audit-logs`
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
