# Isabella - Endpoints de Empresa

## Revision

Los endpoints de empresa estan implementados en `TechMarket-IA` bajo `CompanyPortalController`, con base `/api/empresa`.

Autenticacion local:

- Preferido: `X-User-Id`, resuelto contra `tenant_members`.
- Alternativa para pruebas: `X-Tenant-Id` o `X-Company-Id`, aceptando UUID puro o `EMP-{uuid}`.

Persistencia usada:

- Perfil: `tenants`, `tenant_profiles`, `branches`.
- Publicaciones: `feed_posts`.
- Productos, servicios y ofertas: `listings` + `feed_posts.company_listing_id`.
- Likes: `company_publication_likes`.
- Comentarios: `post_comments`.
- Encuestas: `feed_posts` con `post_type = 'survey'` y opciones en `options_json`.

Verificacion ejecutada:

```bash
mvn test -DskipTests -Dspotless.check.skip=true
```

Resultado: compila correctamente en `TechMarket-IA`.

## Headers

Usar uno de estos contextos:

```http
X-User-Id: 10000000-0000-0000-0000-000000000101
```

o:

```http
X-Tenant-Id: EMP-50000000-0000-0000-0000-000000000101
```

## Perfil

### GET `/api/empresa/perfil`

Devuelve:

```json
{
  "id": "EMP-{uuid}",
  "name": "Andes Tech Store",
  "logoUrl": "https://cdn.techmarket.local/tenants/andes-tech/logo.png",
  "logoText": "AT",
  "slogan": "Tecnologia para trabajar mejor",
  "specialization": "Hardware y perifericos",
  "category": "Retail tecnologico",
  "businessType": "RETAIL",
  "rating": 4.7,
  "reviewCount": 38,
  "experienceYears": 5,
  "description": "Catalogo curado de tecnologia.",
  "about": "Empresa especializada en laptops, componentes y accesorios.",
  "addresses": [],
  "coverageAreas": [],
  "contacts": [],
  "socialLinks": [],
  "schedules": [],
  "branches": [],
  "locationOverview": {},
  "settings": {
    "editable": true,
    "visibility": "public"
  }
}
```

### PUT `/api/empresa/perfil`

Body:

```json
{
  "name": "Andes Tech Store",
  "logoUrl": "/uploads/empresa/logo.png",
  "logoText": "AT",
  "slogan": "Tecnologia para trabajar mejor",
  "specialization": "Hardware, redes y soporte",
  "category": "Retail tecnologico",
  "businessType": "RETAIL",
  "rating": 4.8,
  "reviewCount": 42,
  "experienceYears": 6,
  "description": "Venta y soporte tecnico especializado.",
  "about": "Atendemos hogares, profesionales y empresas.",
  "addresses": [],
  "coverageAreas": ["La Paz", "El Alto"],
  "contacts": [],
  "socialLinks": [],
  "schedules": [],
  "branches": [],
  "locationOverview": {},
  "settings": {
    "editable": true
  }
}
```

## Resumen

### GET `/api/empresa/resumen`

Devuelve panel, radar, metricas, configuracion de IA, alertas, actividad reciente, acciones recomendadas y `settings`.

### POST `/api/empresa/ia/consulta`

Body:

```json
{
  "question": "Que producto deberia promocionar esta semana?",
  "context": "catalogo y publicaciones"
}
```

Response:

```json
{
  "summary": "Analisis generado para: Que producto deberia promocionar esta semana?",
  "dataPoints": [],
  "advice": "Prioriza una publicacion clara con precio, imagen y estado activo...",
  "nextStep": "Publica o actualiza el producto con mayor demanda..."
}
```

## Publicaciones

### GET `/api/empresa/publicaciones`

Devuelve una vista agregada:

```json
{
  "company": {},
  "summary": {},
  "filters": [],
  "feed": [],
  "textPosts": [],
  "products": [],
  "services": [],
  "offers": [],
  "surveys": [],
  "settings": {}
}
```

### Publicaciones de texto

`textPosts`:

```json
{
  "id": "POST-{uuid}",
  "title": "Atencion tecnica sin costo de evaluacion",
  "message": "Si tu equipo esta lento, escribenos por chat...",
  "date": "2026-05-14T09:00:00Z",
  "imageUrl": "/uploads/publicaciones/texto-1.jpg",
  "company": {
    "id": "EMP-{uuid}",
    "name": "Andes Tech Store",
    "logoUrl": "/uploads/empresa/logo.png",
    "logoText": "AT"
  },
  "social": {
    "likes": 0,
    "liked": false,
    "commentsCount": 0
  },
  "actions": {
    "canContact": true,
    "canLike": true,
    "canComment": true,
    "canSend": true
  }
}
```

Crear texto:

```http
POST /api/empresa/publicaciones
```

```json
{
  "type": "text",
  "title": "Atencion tecnica sin costo de evaluacion",
  "description": "Si tu equipo esta lento, escribenos por chat y te orientamos con una primera revision sin compromiso.",
  "imageUrl": "/uploads/publicaciones/texto-1.jpg"
}
```

### Productos

`products`:

```json
{
  "id": "PROD-{uuid}",
  "name": "Laptop ThinkPad E14",
  "description": "Equipo profesional para oficina.",
  "price": 7200.0,
  "status": "ACTIVE",
  "imageUrl": "/uploads/productos/laptop.jpg",
  "company": {},
  "publication": {
    "id": "POST-{uuid}",
    "type": "product",
    "tag": "Producto",
    "date": "2026-05-14T09:00:00Z",
    "title": "Laptop ThinkPad E14",
    "description": "Equipo profesional para oficina.",
    "imageUrl": "/uploads/productos/laptop.jpg"
  },
  "metrics": []
}
```

Crear producto:

```json
{
  "type": "product",
  "title": "Laptop ThinkPad E14",
  "description": "Equipo profesional para oficina.",
  "price": 7200.0,
  "imageUrl": "/uploads/productos/laptop.jpg",
  "status": "ACTIVE"
}
```

Actualizar producto:

```http
PUT /api/empresa/productos/PROD-{uuid}
```

```json
{
  "id": "PROD-{uuid}",
  "name": "Laptop ThinkPad E14",
  "description": "Equipo profesional actualizado.",
  "price": 6990.0,
  "status": "ACTIVE",
  "imageUrl": "/uploads/productos/laptop-v2.jpg"
}
```

### Servicios

`services`:

```json
{
  "id": "SERV-{uuid}",
  "name": "Mantenimiento preventivo",
  "description": "Limpieza, diagnostico y optimizacion.",
  "price": 180.0,
  "imageUrl": "/uploads/servicios/mantenimiento.jpg",
  "company": {},
  "publication": {},
  "metrics": []
}
```

Crear servicio:

```json
{
  "type": "service",
  "title": "Mantenimiento preventivo",
  "description": "Limpieza, diagnostico y optimizacion.",
  "price": 180.0,
  "imageUrl": "/uploads/servicios/mantenimiento.jpg"
}
```

Actualizar servicio:

```http
PUT /api/empresa/servicios/SERV-{uuid}
```

```json
{
  "id": "SERV-{uuid}",
  "name": "Mantenimiento preventivo premium",
  "description": "Incluye diagnostico, limpieza y reporte.",
  "price": 220.0,
  "imageUrl": "/uploads/servicios/mantenimiento-premium.jpg"
}
```

### Ofertas

`offers`:

```json
{
  "id": "OFF-{uuid}",
  "title": "Combo upgrade SSD",
  "description": "SSD 1TB con instalacion incluida.",
  "currentPrice": 520.0,
  "previousPrice": 650.0,
  "label": "15% OFF",
  "imageUrl": "/uploads/ofertas/ssd.jpg",
  "company": {},
  "publication": {},
  "metrics": []
}
```

Crear oferta:

```json
{
  "type": "offer",
  "title": "Combo upgrade SSD",
  "description": "SSD 1TB con instalacion incluida.",
  "currentPrice": 520.0,
  "previousPrice": 650.0,
  "label": "15% OFF",
  "imageUrl": "/uploads/ofertas/ssd.jpg"
}
```

Actualizar oferta:

```http
PUT /api/empresa/ofertas/OFF-{uuid}
```

```json
{
  "id": "OFF-{uuid}",
  "title": "Combo upgrade SSD",
  "description": "SSD 1TB con instalacion y migracion.",
  "currentPrice": 499.0,
  "previousPrice": 650.0,
  "label": "Oferta limitada",
  "imageUrl": "/uploads/ofertas/ssd-v2.jpg"
}
```

### Encuestas

`surveys`:

```json
{
  "id": "SURV-{uuid}",
  "question": "Que servicio necesitas esta semana?",
  "votes": 0,
  "company": {},
  "options": [
    {
      "id": "OPT-1",
      "text": "Mantenimiento",
      "percent": 0,
      "votes": 0
    }
  ],
  "publication": {
    "id": "POST-{uuid}",
    "type": "survey",
    "tag": "Encuesta",
    "date": "2026-05-14T09:00:00Z",
    "title": "Que servicio necesitas esta semana?"
  }
}
```

Crear encuesta:

```http
POST /api/empresa/encuestas
```

```json
{
  "question": "Que servicio necesitas esta semana?",
  "options": [
    "Mantenimiento",
    "Instalacion de redes",
    "Upgrade de laptop"
  ]
}
```

Tambien acepta opciones con votos:

```json
{
  "question": "Que promocion prefieres?",
  "options": [
    {
      "text": "Descuento en SSD",
      "votes": 8
    },
    {
      "text": "Revision gratuita",
      "votes": 12
    }
  ]
}
```

## Interacciones

### POST `/api/empresa/publicaciones/{id}/likes`

```json
{
  "publicationId": "POST-{uuid}",
  "liked": true
}
```

### POST `/api/empresa/publicaciones/{id}/comentarios`

```json
{
  "publicationId": "POST-{uuid}",
  "authorName": "Cliente",
  "text": "Me interesa recibir mas informacion."
}
```

## Imagenes

### POST `/api/empresa/archivos/imagenes`

JSON:

```json
{
  "file": "foto.jpg",
  "folder": "servicios"
}
```

Multipart:

```http
POST /api/empresa/archivos/imagenes
Content-Type: multipart/form-data
```

Campos:

- `file`: archivo de imagen.
- `folder`: `empresa`, `productos`, `servicios`, `ofertas` o `publicaciones`.

Response:

```json
{
  "url": "https://cdn.techmarket.local/empresa/servicios/{uuid}.jpg",
  "folder": "servicios"
}
```
