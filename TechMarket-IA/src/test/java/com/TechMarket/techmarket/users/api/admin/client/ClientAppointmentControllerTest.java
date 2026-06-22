package com.techmarket.techmarket.users.api.admin.client;

import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.techmarket.techmarket.security.jwt.JwtTokenProvider;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.entity.SpecialistServiceAppointmentJpaEntity;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.ClientAppointmentSummaryProjection;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistServiceAppointmentSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientChatJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.ClientChatSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.UserSpringDataRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ClientAppointmentController.class)
@AutoConfigureMockMvc(addFilters = false)
class ClientAppointmentControllerTest {

    private static final UUID CLIENT_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID SPECIALIST_ID =
            UUID.fromString("22222222-2222-2222-2222-222222222222");

    @Autowired private MockMvc mockMvc;

    @MockBean private ClientChatSpringDataRepository ticketRepository;
    @MockBean private SpecialistServiceAppointmentSpringDataRepository appointmentRepository;
    @MockBean private UserSpringDataRepository userRepository;

    // Requerido para construir los filtros servlet que @WebMvcTest registra.
    @MockBean private JwtTokenProvider jwtTokenProvider;

    @Test
    void create_shouldPersistTicketAndPendingAppointment() throws Exception {
        when(userRepository.existsById(SPECIALIST_ID)).thenReturn(true);
        when(ticketRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(appointmentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(
                        post("/api/clients/appointments")
                                .header("X-User-Id", CLIENT_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"especialistaId\":\"USR-"
                                                + SPECIALIST_ID
                                                + "\",\"servicio\":\"Reparacion de laptop\","
                                                + "\"fecha\":\"2026-07-01\",\"hora\":\"15:30\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", startsWith("CITA-")))
                .andExpect(jsonPath("$.estado").value("pendiente"));

        ArgumentCaptor<SpecialistServiceAppointmentJpaEntity> captor =
                ArgumentCaptor.forClass(SpecialistServiceAppointmentJpaEntity.class);
        verify(appointmentRepository).save(captor.capture());
        SpecialistServiceAppointmentJpaEntity saved = captor.getValue();
        org.junit.jupiter.api.Assertions.assertEquals("pendiente", saved.getStatus());
        org.junit.jupiter.api.Assertions.assertEquals(
                SPECIALIST_ID, saved.getAssignedTechnicianUserId());
        org.junit.jupiter.api.Assertions.assertEquals(
                "2026-07-01T15:30", saved.getStartAt().toLocalDateTime().toString());

        ArgumentCaptor<ClientChatJpaEntity> ticketCaptor =
                ArgumentCaptor.forClass(ClientChatJpaEntity.class);
        verify(ticketRepository).save(ticketCaptor.capture());
        org.junit.jupiter.api.Assertions.assertEquals(
                CLIENT_ID, ticketCaptor.getValue().getCustomerUserId());
        org.junit.jupiter.api.Assertions.assertEquals(
                "APPOINTMENT", ticketCaptor.getValue().getTicketType());
    }

    @Test
    void create_shouldReturnNotFoundForUnknownSpecialist() throws Exception {
        when(userRepository.existsById(SPECIALIST_ID)).thenReturn(false);

        mockMvc.perform(
                        post("/api/clients/appointments")
                                .header("X-User-Id", CLIENT_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"especialistaId\":\"USR-"
                                                + SPECIALIST_ID
                                                + "\",\"fecha\":\"2026-07-01\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_shouldRequireUserHeader() throws Exception {
        mockMvc.perform(
                        post("/api/clients/appointments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"especialistaId\":\"USR-"
                                                + SPECIALIST_ID
                                                + "\",\"fecha\":\"2026-07-01\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void appointments_shouldReturnClientAppointmentsWithNormalizedStatus() throws Exception {
        when(appointmentRepository.findAppointmentsByCustomerUserId(CLIENT_ID))
                .thenReturn(List.of(appointment("aceptado", "2026-07-01T15:30:00Z")));

        mockMvc.perform(get("/api/clients/appointments").header("X-User-Id", CLIENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", startsWith("CITA-")))
                .andExpect(jsonPath("$[0].especialista").value("Alejandro Torres"))
                .andExpect(jsonPath("$[0].servicio").value("Reparacion de laptop"))
                .andExpect(jsonPath("$[0].fecha").value("2026-07-01"))
                .andExpect(jsonPath("$[0].hora").value("15:30"))
                .andExpect(jsonPath("$[0].estado").value("aceptada"));
    }

    private ClientAppointmentSummaryProjection appointment(String status, String startAt) {
        return new ClientAppointmentSummaryProjection() {
            @Override
            public UUID getId() {
                return UUID.randomUUID();
            }

            @Override
            public String getServiceName() {
                return "Reparacion de laptop";
            }

            @Override
            public String getStatus() {
                return status;
            }

            @Override
            public String getStartAt() {
                return startAt;
            }

            @Override
            public String getLocation() {
                return "Domicilio";
            }

            @Override
            public String getNotes() {
                return "Traer cargador";
            }

            @Override
            public String getTechnicianFirstName() {
                return "Alejandro";
            }

            @Override
            public String getTechnicianLastName() {
                return "Torres";
            }
        };
    }
}
