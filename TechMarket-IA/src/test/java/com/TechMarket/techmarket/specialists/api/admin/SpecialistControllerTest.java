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
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.entity.SpecialistPortfolioItemJpaEntity;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.entity.SpecialistServiceJpaEntity;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistAvailabilitySpringDataRepository;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistPortfolioItemSpringDataRepository;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistProfileSpringDataRepository;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistReviewStatsProjection;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistReviewStatsSpringDataRepository;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistServiceAppointmentSpringDataRepository;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistServiceSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.UserJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.UserSpringDataRepository;
import java.math.BigDecimal;
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
            SpecialistAvailabilityController.class
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
