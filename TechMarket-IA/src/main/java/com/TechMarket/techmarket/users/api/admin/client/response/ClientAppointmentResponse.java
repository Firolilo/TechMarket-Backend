package com.techmarket.techmarket.users.api.admin.client.response;

public record ClientAppointmentResponse(
        String id,
        String especialista,
        String servicio,
        String fecha,
        String hora,
        String estado,
        String ubicacion,
        String notas) {}
