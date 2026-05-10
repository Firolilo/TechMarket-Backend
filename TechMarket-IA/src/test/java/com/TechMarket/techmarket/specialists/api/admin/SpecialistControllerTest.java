package com.techmarket.techmarket.specialists.api.admin;

import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techmarket.techmarket.specialists.application.service.SpecialistIdentitySupport;
import com.techmarket.techmarket.specialists.application.service.SpecialistJsonListMapper;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.entity.SpecialistCalendarBlockJpaEntity;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.entity.SpecialistFileJpaEntity;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.entity.SpecialistPortfolioItemJpaEntity;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.entity.SpecialistServiceJpaEntity;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.entity.SpecialistServiceAppointmentJpaEntity;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.entity.SpecialistTransactionJpaEntity;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistAppointmentSummaryProjection;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistAvailabilitySpringDataRepository;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistAiQuerySpringDataRepository;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistCalendarBlockSpringDataRepository;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistCertificationSpringDataRepository;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistFileSpringDataRepository;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistPortfolioItemSpringDataRepository;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistProfileSpringDataRepository;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistReviewSpringDataRepository;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistReviewStatsProjection;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistReviewStatsSpringDataRepository;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistServiceAppointmentSpringDataRepository;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistServiceSpringDataRepository;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistTransactionSpringDataRepository;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistWithdrawalSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientChatAttachmentJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientChatJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientChatMessageJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.ClientChatAttachmentSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.ClientChatMessageSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.ClientChatReadReceiptSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.ClientChatSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.UserJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.UserSpringDataRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
        {
            SpecialistProfileController.class,
            SpecialistServiceController.class,
            SpecialistPortfolioController.class,
            SpecialistAvailabilityController.class,
            SpecialistCalendarController.class,
            SpecialistRequestController.class,
            SpecialistProjectController.class,
            SpecialistChatController.class,
            SpecialistFileController.class,
            SpecialistWalletController.class,
            SpecialistReviewController.class,
            SpecialistCertificationController.class,
            SpecialistAiController.class
        })
@Import({SpecialistIdentitySupport.class, SpecialistJsonListMapper.class})
class SpecialistControllerTest {

    private static final UUID USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockBean private UserSpringDataRepository userRepository;

    @MockBean private SpecialistProfileSpringDataRepository profileRepository;

    @MockBean private SpecialistServiceSpringDataRepository serviceRepository;

    @MockBean private SpecialistPortfolioItemSpringDataRepository portfolioRepository;

    @MockBean private SpecialistAvailabilitySpringDataRepository availabilityRepository;

    @MockBean private SpecialistServiceAppointmentSpringDataRepository appointmentRepository;

    @MockBean private SpecialistReviewStatsSpringDataRepository reviewStatsRepository;

    @MockBean private SpecialistCalendarBlockSpringDataRepository calendarBlockRepository;

    @MockBean private ClientChatSpringDataRepository chatRepository;

    @MockBean private ClientChatAttachmentSpringDataRepository chatAttachmentRepository;

    @MockBean private ClientChatMessageSpringDataRepository chatMessageRepository;

    @MockBean private ClientChatReadReceiptSpringDataRepository chatReadReceiptRepository;

    @MockBean private SpecialistFileSpringDataRepository specialistFileRepository;

    @MockBean private SpecialistTransactionSpringDataRepository transactionRepository;

    @MockBean private SpecialistWithdrawalSpringDataRepository withdrawalRepository;

    @MockBean private SpecialistReviewSpringDataRepository specialistReviewRepository;

    @MockBean private SpecialistCertificationSpringDataRepository certificationRepository;

    @MockBean private SpecialistAiQuerySpringDataRepository aiQueryRepository;

    @Test
    void profile_shouldRequireUserHeader() throws Exception {
        mockMvc.perform(get("/api/specialists/profile")).andExpect(status().isUnauthorized());
    }

    @Test
    void profile_shouldRejectInvalidUserHeader() throws Exception {
        mockMvc.perform(get("/api/specialists/profile").header("X-User-Id", "not-a-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void stats_shouldReturnCompletedJobsAndReviewAverage() throws Exception {
        mockUser();
        when(appointmentRepository.countCompletedByTechnicianUserId(USER_ID)).thenReturn(286L);
        when(reviewStatsRepository.findStatsByTechnicianUserId(USER_ID))
                .thenReturn(reviewStats(112L, new BigDecimal("4.75")));

        mockMvc.perform(get("/api/specialists/profile/stats").header("X-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trabajosCompletados").value(286))
                .andExpect(jsonPath("$.totalResenas").value(112))
                .andExpect(jsonPath("$.calificacionPromedio").value(4.8));
    }

    @Test
    void updatePhoto_shouldPersistPhotoUrl() throws Exception {
        mockUser();
        when(profileRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());
        when(profileRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(
                        post("/api/specialists/profile/photo")
                                .header("X-User-Id", USER_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json(new LinkedHashMap<>() {
                                    {
                                        put(
                                                "url",
                                                "https://cdn.techmarket.bo/specialists/tec-avatar.jpg");
                                    }
                                })))
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.url")
                                .value(
                                        "https://cdn.techmarket.bo/specialists/tec-avatar.jpg"));
    }

    @Test
    void createService_shouldPersistOwnedService() throws Exception {
        mockUser();
        when(serviceRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(
                        post("/api/specialists/services")
                                .header("X-User-Id", USER_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json(servicePayload())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", startsWith("SERV-")))
                .andExpect(jsonPath("$.nombre").value("Armado de PC escritorio"))
                .andExpect(jsonPath("$.mensaje").value("Servicio creado exitosamente"));
    }

    @Test
    void updateService_shouldUpdateOwnedService() throws Exception {
        mockUser();
        UUID serviceId = UUID.randomUUID();
        when(serviceRepository.findByIdAndUserId(serviceId, USER_ID))
                .thenReturn(Optional.of(service(serviceId)));
        when(serviceRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(
                        put("/api/specialists/services/SERV-" + serviceId)
                                .header("X-User-Id", USER_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json(new LinkedHashMap<>() {
                                    {
                                        put("nombre", "Diagnostico avanzado");
                                    }
                                })))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Servicio actualizado"));
    }

    @Test
    void updateService_shouldNotUpdateForeignService() throws Exception {
        mockUser();
        UUID serviceId = UUID.randomUUID();
        when(serviceRepository.findByIdAndUserId(serviceId, USER_ID)).thenReturn(Optional.empty());

        mockMvc.perform(
                        put("/api/specialists/services/" + serviceId)
                                .header("X-User-Id", USER_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json(new LinkedHashMap<>() {
                                    {
                                        put("nombre", "Otro");
                                    }
                                })))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteService_shouldDeleteOwnedService() throws Exception {
        mockUser();
        UUID serviceId = UUID.randomUUID();
        SpecialistServiceJpaEntity service = service(serviceId);
        when(serviceRepository.findByIdAndUserId(serviceId, USER_ID)).thenReturn(Optional.of(service));

        mockMvc.perform(delete("/api/specialists/services/SERV-" + serviceId).header("X-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Servicio eliminado"));

        verify(serviceRepository).delete(service);
    }

    @Test
    void deletePortfolioItem_shouldDeleteOwnedItem() throws Exception {
        mockUser();
        UUID itemId = UUID.randomUUID();
        SpecialistPortfolioItemJpaEntity item = portfolioItem(itemId);
        when(portfolioRepository.findByIdAndUserId(itemId, USER_ID)).thenReturn(Optional.of(item));

        mockMvc.perform(delete("/api/specialists/portfolio/PORT-" + itemId).header("X-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Trabajo eliminado del portafolio"));

        verify(portfolioRepository).delete(item);
    }

    @Test
    void updateAvailability_shouldPersistConfiguration() throws Exception {
        mockUser();
        when(availabilityRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());
        when(availabilityRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        LinkedHashMap<String, Object> payload = new LinkedHashMap<>();
        payload.put("estado", "disponible");
        payload.put("dias", List.of("lunes", "martes"));
        payload.put("inicio", "09:00");
        payload.put("fin", "17:00");
        payload.put("modalidad", List.of("remoto"));
        payload.put("cobertura", "Santa Cruz");

        mockMvc.perform(
                        put("/api/specialists/availability")
                                .header("X-User-Id", USER_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json(payload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Disponibilidad actualizada"));
    }

    @Test
    void updateAvailabilityStatus_shouldRejectInvalidStatus() throws Exception {
        mockUser();
        when(availabilityRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

        mockMvc.perform(
                        patch("/api/specialists/availability/status")
                                .header("X-User-Id", USER_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json(new LinkedHashMap<>() {
                                    {
                                        put("estado", "viajando");
                                        put("tiempoRespuesta", "45 min");
                                    }
                                })))
                .andExpect(status().isBadRequest());
    }

    @Test
    void calendar_shouldReturnAppointmentsAndBlocks() throws Exception {
        mockUser();
        UUID appointmentId = UUID.randomUUID();
        UUID blockId = UUID.randomUUID();
        when(appointmentRepository.findCalendarByTechnicianUserId(USER_ID))
                .thenReturn(List.of(appointmentSummary(appointmentId, "confirmada")));
        when(calendarBlockRepository.findAllByUserIdOrderByBlockDateAscStartTimeAsc(USER_ID))
                .thenReturn(List.of(calendarBlock(blockId)));

        mockMvc.perform(get("/api/specialists/calendar").header("X-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("PROJ-" + appointmentId))
                .andExpect(jsonPath("$[0].cliente").value("Carlos Mendez"))
                .andExpect(jsonPath("$[1].id").value("BLK-" + blockId))
                .andExpect(jsonPath("$[1].estado").value("bloqueado"));
    }

    @Test
    void createCalendarBlock_shouldPersistOwnedBlock() throws Exception {
        mockUser();
        when(calendarBlockRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(
                        post("/api/specialists/calendar/blocks")
                                .header("X-User-Id", USER_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json(new LinkedHashMap<>() {
                                    {
                                        put("fecha", "2026-04-10");
                                        put("hora", "10:00");
                                        put("fin", "11:00");
                                        put("motivo", "Visita tecnica");
                                    }
                                })))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Bloque agregado a la agenda"));
    }

    @Test
    void respondRequest_shouldAcceptOwnedRequest() throws Exception {
        mockUser();
        UUID requestId = UUID.randomUUID();
        when(appointmentRepository.findByIdAndAssignedTechnicianUserId(requestId, USER_ID))
                .thenReturn(Optional.of(appointment(requestId)));
        when(appointmentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(
                        patch("/api/specialists/requests/REQ-" + requestId + "/respond")
                                .header("X-User-Id", USER_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json(new LinkedHashMap<>() {
                                    {
                                        put("accion", "aceptar");
                                    }
                                })))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accion").value("aceptada"));
    }

    @Test
    void updateProjectStatus_shouldPersistAllowedStatus() throws Exception {
        mockUser();
        UUID projectId = UUID.randomUUID();
        when(appointmentRepository.findByIdAndAssignedTechnicianUserId(projectId, USER_ID))
                .thenReturn(Optional.of(appointment(projectId)));
        when(appointmentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(
                        patch("/api/specialists/projects/PROJ-" + projectId + "/status")
                                .header("X-User-Id", USER_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json(new LinkedHashMap<>() {
                                    {
                                        put("estado", "completado");
                                    }
                                })))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("completado"))
                .andExpect(jsonPath("$.mensaje").value("Proyecto marcado como completado"));
    }

    @Test
    void specialistChats_shouldReturnAssignedChats() throws Exception {
        mockUser();
        UUID clientId = UUID.randomUUID();
        UUID chatId = UUID.randomUUID();
        ClientChatJpaEntity chat = chat(chatId, clientId);
        ClientChatMessageJpaEntity message = chatMessage(chatId, clientId);
        when(chatRepository.findAllByAssignedTechnicianUserIdAndTicketTypeOrderByCreatedAtDesc(
                        USER_ID, "CHAT"))
                .thenReturn(List.of(chat));
        when(chatMessageRepository.findTopByTicketIdOrderByCreatedAtDesc(chatId))
                .thenReturn(Optional.of(message));
        when(chatReadReceiptRepository.findByTicketIdAndUserId(chatId, USER_ID))
                .thenReturn(Optional.empty());
        when(chatMessageRepository.countByTicketIdAndAuthorUserIdNot(chatId, USER_ID)).thenReturn(2L);
        when(userRepository.findById(clientId)).thenReturn(Optional.of(client(clientId)));

        mockMvc.perform(get("/api/specialists/chats").header("X-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("CHT-" + chatId))
                .andExpect(jsonPath("$[0].cliente").value("Carlos Mendez"))
                .andExpect(jsonPath("$[0].ultimoMensaje").value("A que hora llegas?"))
                .andExpect(jsonPath("$[0].noLeidos").value(2));
    }

    @Test
    void specialistChatMessages_shouldReturnAssignedConversationMessages() throws Exception {
        mockUser();
        UUID clientId = UUID.randomUUID();
        UUID chatId = UUID.randomUUID();
        when(chatRepository.findByIdAndAssignedTechnicianUserId(chatId, USER_ID))
                .thenReturn(Optional.of(chat(chatId, clientId)));
        when(chatMessageRepository.findAllByTicketIdOrderByCreatedAtAsc(chatId))
                .thenReturn(List.of(chatMessage(chatId, clientId)));

        mockMvc.perform(get("/api/specialists/chats/CHT-" + chatId).header("X-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].remitente").value("cliente"))
                .andExpect(jsonPath("$[0].contenido").value("A que hora llegas?"));
    }

    @Test
    void createSpecialistFile_shouldPersistOwnedFile() throws Exception {
        mockUser();
        when(specialistFileRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(
                        post("/api/specialists/files")
                                .header("X-User-Id", USER_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json(new LinkedHashMap<>() {
                                    {
                                        put("url", "https://cdn.techmarket.bo/files/certificado.pdf");
                                        put("nombre", "certificado.pdf");
                                        put("tamano", "1.1 MB");
                                    }
                                })))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", startsWith("FILE-")))
                .andExpect(jsonPath("$.mensaje").value("Archivo subido correctamente"));
    }

    @Test
    void wallet_shouldReturnAggregatedAmounts() throws Exception {
        mockUser();
        when(transactionRepository.sumAvailableByUserId(USER_ID)).thenReturn(new BigDecimal("108.00"));
        when(transactionRepository.sumCompletedGrossByUserId(USER_ID)).thenReturn(new BigDecimal("120.00"));
        when(transactionRepository.sumInProcessByUserId(USER_ID)).thenReturn(new BigDecimal("50.00"));

        mockMvc.perform(get("/api/specialists/wallet").header("X-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldoDisponible").value("Bs 108.00"))
                .andExpect(jsonPath("$.ingresosTotales").value("Bs 120.00"))
                .andExpect(jsonPath("$.enProceso").value("Bs 50.00"));
    }

    @Test
    void withdraw_shouldPersistRequest() throws Exception {
        mockUser();
        when(withdrawalRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(
                        post("/api/specialists/wallet/withdraw")
                                .header("X-User-Id", USER_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json(new LinkedHashMap<>() {
                                    {
                                        put("monto", new BigDecimal("500.00"));
                                    }
                                })))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.monto").value("Bs 500.00"))
                .andExpect(jsonPath("$.mensaje").value("Solicitud de retiro enviada"));
    }

    @Test
    void aiQuery_shouldReturnActionPlan() throws Exception {
        mockUser();
        when(aiQueryRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(
                        post("/api/specialists/ai/query")
                                .header("X-User-Id", USER_ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json(new LinkedHashMap<>() {
                                    {
                                        put("consulta", "Que servicios debo priorizar?");
                                    }
                                })))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.consulta").value("Que servicios debo priorizar?"))
                .andExpect(jsonPath("$.respuesta.foco").value("disponibilidad"));
    }

    private void mockUser() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user()));
    }

    private UserJpaEntity user() {
        UserJpaEntity user = new UserJpaEntity();
        user.setId(USER_ID);
        user.setFirstName("Alejandro");
        user.setLastName("Torres");
        user.setEmail("alejandro@techmarket.com");
        return user;
    }

    private SpecialistServiceJpaEntity service(UUID id) {
        SpecialistServiceJpaEntity service = new SpecialistServiceJpaEntity();
        service.setId(id);
        service.setUserId(USER_ID);
        service.setName("Reparacion de laptops");
        service.setDescription("Diagnostico detallado");
        service.setPrice(new BigDecimal("120.00"));
        service.setCurrency("Bs");
        service.setServiceType("Reparacion");
        service.setCreatedAt(OffsetDateTime.now());
        service.setUpdatedAt(OffsetDateTime.now());
        return service;
    }

    private SpecialistPortfolioItemJpaEntity portfolioItem(UUID id) {
        SpecialistPortfolioItemJpaEntity item = new SpecialistPortfolioItemJpaEntity();
        item.setId(id);
        item.setUserId(USER_ID);
        item.setTitle("Laptop con sobrecalentamiento");
        return item;
    }

    private SpecialistServiceAppointmentJpaEntity appointment(UUID id) {
        SpecialistServiceAppointmentJpaEntity appointment = new SpecialistServiceAppointmentJpaEntity();
        appointment.setId(id);
        appointment.setAssignedTechnicianUserId(USER_ID);
        appointment.setStatus("pendiente");
        return appointment;
    }

    private SpecialistCalendarBlockJpaEntity calendarBlock(UUID id) {
        SpecialistCalendarBlockJpaEntity block = new SpecialistCalendarBlockJpaEntity();
        block.setId(id);
        block.setUserId(USER_ID);
        block.setBlockDate(LocalDate.parse("2026-04-10"));
        block.setStartTime("10:00");
        block.setReason("Agenda bloqueada");
        return block;
    }

    private ClientChatJpaEntity chat(UUID id, UUID clientId) {
        ClientChatJpaEntity chat = new ClientChatJpaEntity();
        chat.setId(id);
        chat.setCustomerUserId(clientId);
        chat.setAssignedTechnicianUserId(USER_ID);
        chat.setTicketType("CHAT");
        chat.setSubject("Mantenimiento preventivo");
        chat.setCreatedAt(OffsetDateTime.parse("2026-04-10T09:00:00Z"));
        return chat;
    }

    private ClientChatMessageJpaEntity chatMessage(UUID chatId, UUID authorId) {
        ClientChatMessageJpaEntity message = new ClientChatMessageJpaEntity();
        message.setId(UUID.randomUUID());
        message.setTicketId(chatId);
        message.setAuthorUserId(authorId);
        message.setMessageBody("A que hora llegas?");
        message.setCreatedAt(OffsetDateTime.parse("2026-04-10T09:30:00Z"));
        return message;
    }

    private ClientChatAttachmentJpaEntity chatAttachment(UUID chatId) {
        ClientChatAttachmentJpaEntity attachment = new ClientChatAttachmentJpaEntity();
        attachment.setId(UUID.randomUUID());
        attachment.setTicketId(chatId);
        attachment.setOriginalFileName("diagnostico.pdf");
        attachment.setFileSize("2.4 MB");
        attachment.setCreatedAt(OffsetDateTime.parse("2026-04-09T10:00:00Z"));
        return attachment;
    }

    private SpecialistFileJpaEntity specialistFile(UUID id) {
        SpecialistFileJpaEntity file = new SpecialistFileJpaEntity();
        file.setId(id);
        file.setUserId(USER_ID);
        file.setFileName("certificado_cisco.pdf");
        file.setFileSize("1.1 MB");
        file.setCreatedAt(OffsetDateTime.parse("2026-03-15T10:00:00Z"));
        return file;
    }

    private SpecialistTransactionJpaEntity transaction(UUID id) {
        SpecialistTransactionJpaEntity transaction = new SpecialistTransactionJpaEntity();
        transaction.setId(id);
        transaction.setUserId(USER_ID);
        transaction.setServiceName("Reparacion de laptops");
        transaction.setClientName("Laura Paredes");
        transaction.setAmount(new BigDecimal("120.00"));
        transaction.setPlatformCommission(new BigDecimal("12.00"));
        transaction.setCurrency("Bs");
        transaction.setStatus("completado");
        transaction.setTransactionDate(OffsetDateTime.parse("2026-04-08T10:00:00Z"));
        return transaction;
    }

    private UserJpaEntity client(UUID id) {
        UserJpaEntity user = new UserJpaEntity();
        user.setId(id);
        user.setFirstName("Carlos");
        user.setLastName("Mendez");
        user.setEmail("carlos@techmarket.com");
        return user;
    }

    private SpecialistAppointmentSummaryProjection appointmentSummary(UUID id, String status) {
        return new SpecialistAppointmentSummaryProjection() {
            @Override
            public UUID getId() {
                return id;
            }

            @Override
            public UUID getTicketId() {
                return UUID.randomUUID();
            }

            @Override
            public String getCustomerFirstName() {
                return "Carlos";
            }

            @Override
            public String getCustomerLastName() {
                return "Mendez";
            }

            @Override
            public String getCustomerPhone() {
                return "+591 70000000";
            }

            @Override
            public String getServiceName() {
                return "Mantenimiento preventivo";
            }

            @Override
            public String getDescription() {
                return "Limpieza interna";
            }

            @Override
            public String getPriority() {
                return "alta";
            }

            @Override
            public String getStatus() {
                return status;
            }

            @Override
            public OffsetDateTime getStartAt() {
                return OffsetDateTime.parse("2026-04-10T10:00:00Z");
            }

            @Override
            public String getLocation() {
                return "Domicilio";
            }

            @Override
            public String getNotes() {
                return "Notas";
            }
        };
    }

    private SpecialistReviewStatsProjection reviewStats(Long total, BigDecimal average) {
        return new SpecialistReviewStatsProjection() {
            @Override
            public Long getTotalReviews() {
                return total;
            }

            @Override
            public BigDecimal getAverageRating() {
                return average;
            }
        };
    }

    private LinkedHashMap<String, Object> servicePayload() {
        LinkedHashMap<String, Object> payload = new LinkedHashMap<>();
        payload.put("nombre", "Armado de PC escritorio");
        payload.put("descripcion", "Armado y pruebas");
        payload.put("precio", new BigDecimal("250.00"));
        payload.put("tipo", "Reparacion");
        payload.put("destacado", true);
        return payload;
    }

    private String json(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }
}
