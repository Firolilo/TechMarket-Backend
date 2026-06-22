package com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository;

import java.util.UUID;

/** Vista de una cita desde el lado del cliente (incluye al técnico asignado). */
public interface ClientAppointmentSummaryProjection {

    UUID getId();

    String getServiceName();

    String getStatus();

    String getStartAt();

    String getLocation();

    String getNotes();

    String getTechnicianFirstName();

    String getTechnicianLastName();
}
