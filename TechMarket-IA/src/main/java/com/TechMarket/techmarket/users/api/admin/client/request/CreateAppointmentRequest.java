package com.techmarket.techmarket.users.api.admin.client.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Solicitud de un cliente para agendar una cita con un especialista. */
public record CreateAppointmentRequest(
        @NotBlank(message = "especialistaId is required") String especialistaId,
        @Size(max = 255, message = "servicio must have at most 255 chars") String servicio,
        @Size(max = 255, message = "descripcion must have at most 255 chars") String descripcion,
        @NotBlank(message = "fecha is required") String fecha,
        String hora,
        @Size(max = 255, message = "ubicacion must have at most 255 chars") String ubicacion,
        @Size(max = 255, message = "notas must have at most 255 chars") String notas) {}
