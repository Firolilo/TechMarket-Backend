package com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository;

import java.util.UUID;

public interface SpecialistAppointmentSummaryProjection {

    UUID getId();

    UUID getTicketId();

    String getCustomerFirstName();

    String getCustomerLastName();

    String getCustomerPhone();

    String getServiceName();

    String getDescription();

    String getPriority();

    String getStatus();

    String getStartAt();

    String getLocation();

    String getNotes();
}
