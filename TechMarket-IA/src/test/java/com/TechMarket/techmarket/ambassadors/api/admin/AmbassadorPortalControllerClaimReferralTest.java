package com.techmarket.techmarket.ambassadors.api.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorJpaEntity;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorReferralJpaEntity;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.entity.AmbassadorReferralLinkJpaEntity;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorCommissionDisputeSpringDataRepository;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorCommissionSpringDataRepository;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorInvitationSpringDataRepository;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorLeadActivitySpringDataRepository;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorLeadSpringDataRepository;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorOnboardingMilestoneSpringDataRepository;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorOnboardingReminderSpringDataRepository;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorOnboardingTaskSpringDataRepository;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorPayoutMethodSpringDataRepository;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorReferralActivitySpringDataRepository;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorReferralFileSpringDataRepository;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorReferralLinkSpringDataRepository;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorReferralNoteSpringDataRepository;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorReferralSpringDataRepository;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorSpringDataRepository;
import com.techmarket.techmarket.ambassadors.infrastructure.persistence.jpa.repository.AmbassadorWithdrawalSpringDataRepository;
import com.techmarket.techmarket.security.config.SecurityConfig;
import com.techmarket.techmarket.security.jwt.JwtTokenProvider;
import com.techmarket.techmarket.tenants.infrastructure.persistence.jpa.repository.TenantSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.ClientChatMessageSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.ClientChatReadReceiptSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.ClientChatSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.UserSpringDataRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AmbassadorPortalController.class)
@Import(SecurityConfig.class)
class AmbassadorPortalControllerClaimReferralTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockBean private AmbassadorSpringDataRepository ambassadorRepository;
    @MockBean private AmbassadorReferralSpringDataRepository referralRepository;
    @MockBean private AmbassadorCommissionSpringDataRepository commissionRepository;
    @MockBean private AmbassadorReferralLinkSpringDataRepository referralLinkRepository;
    @MockBean private AmbassadorReferralActivitySpringDataRepository activityRepository;
    @MockBean private AmbassadorReferralNoteSpringDataRepository noteRepository;
    @MockBean private AmbassadorReferralFileSpringDataRepository fileRepository;
    @MockBean private AmbassadorOnboardingTaskSpringDataRepository onboardingTaskRepository;
    @MockBean private AmbassadorOnboardingReminderSpringDataRepository reminderRepository;
    @MockBean private AmbassadorOnboardingMilestoneSpringDataRepository milestoneRepository;
    @MockBean private AmbassadorLeadSpringDataRepository leadRepository;
    @MockBean private AmbassadorLeadActivitySpringDataRepository leadActivityRepository;
    @MockBean private AmbassadorCommissionDisputeSpringDataRepository commissionDisputeRepository;
    @MockBean private AmbassadorWithdrawalSpringDataRepository withdrawalRepository;
    @MockBean private AmbassadorPayoutMethodSpringDataRepository payoutMethodRepository;
    @MockBean private AmbassadorInvitationSpringDataRepository invitationRepository;
    @MockBean private ClientChatSpringDataRepository chatRepository;
    @MockBean private ClientChatMessageSpringDataRepository chatMessageRepository;
    @MockBean private ClientChatReadReceiptSpringDataRepository readReceiptRepository;
    @MockBean private UserSpringDataRepository userRepository;
    @MockBean private TenantSpringDataRepository tenantRepository;
    @MockBean private JdbcTemplate jdbcTemplate;

    // Requerido por la cadena de seguridad (JwtAuthenticationFilter) que @WebMvcTest incluye.
    @MockBean private JwtTokenProvider jwtTokenProvider;

    @Test
    void claim_shouldAttributeReferralToReferralLinkOwner() throws Exception {
        UUID ambassadorId = UUID.randomUUID();
        AmbassadorReferralLinkJpaEntity link = referralLink(ambassadorId, 4);
        when(referralLinkRepository.findByCode("EMBA-SCZ")).thenReturn(Optional.of(link));
        when(referralRepository.findByAmbassadorId(ambassadorId)).thenReturn(List.of());
        when(referralRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(
                        post("/api/ambassadors/referrals/claim")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        json(
                                                claimPayload(
                                                        "emba-scz",
                                                        "TechCorp",
                                                        "info@techcorp.bo"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", startsWith("BUS-")))
                .andExpect(jsonPath("$.estado").value("activo"))
                .andExpect(jsonPath("$.mensaje").value("Empresa referida registrada"));

        // El referido queda atribuido al dueño del link y la conversión del link se incrementa.
        ArgumentCaptor<AmbassadorReferralJpaEntity> captor =
                ArgumentCaptor.forClass(AmbassadorReferralJpaEntity.class);
        verify(referralRepository).save(captor.capture());
        assertThat(captor.getValue().getAmbassadorId()).isEqualTo(ambassadorId);
        assertThat(captor.getValue().getStatus()).isEqualTo("activo");
        verify(referralLinkRepository).save(link);
    }

    @Test
    void claim_shouldFallBackToAmbassadorOwnReferralCode() throws Exception {
        UUID ambassadorId = UUID.randomUUID();
        when(referralLinkRepository.findByCode("ANA2026")).thenReturn(Optional.empty());
        when(ambassadorRepository.findByReferralCode("ANA2026"))
                .thenReturn(Optional.of(ambassador(ambassadorId, "ANA2026")));
        when(referralRepository.findByAmbassadorId(ambassadorId)).thenReturn(List.of());
        when(referralRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(
                        post("/api/ambassadors/referrals/claim")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        json(
                                                claimPayload(
                                                        "ana2026",
                                                        "TechCorp",
                                                        "info@techcorp.bo"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("activo"));

        // Sin link: no se incrementa ninguna conversión de link.
        verify(referralLinkRepository, never()).save(any());
    }

    @Test
    void claim_shouldReturn404WhenCodeIsUnknown() throws Exception {
        when(referralLinkRepository.findByCode("NOPE")).thenReturn(Optional.empty());
        when(ambassadorRepository.findByReferralCode("NOPE")).thenReturn(Optional.empty());

        mockMvc.perform(
                        post("/api/ambassadors/referrals/claim")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        json(claimPayload("nope", "TechCorp", "info@techcorp.bo"))))
                .andExpect(status().isNotFound());

        verify(referralRepository, never()).save(any());
    }

    @Test
    void claim_shouldBeIdempotentForSameAmbassadorAndEmail() throws Exception {
        UUID ambassadorId = UUID.randomUUID();
        when(referralLinkRepository.findByCode("ANA2026")).thenReturn(Optional.empty());
        when(ambassadorRepository.findByReferralCode("ANA2026"))
                .thenReturn(Optional.of(ambassador(ambassadorId, "ANA2026")));
        AmbassadorReferralJpaEntity existing = existingReferral("info@techcorp.bo");
        when(referralRepository.findByAmbassadorId(ambassadorId)).thenReturn(List.of(existing));

        mockMvc.perform(
                        post("/api/ambassadors/referrals/claim")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        json(
                                                claimPayload(
                                                        "ana2026",
                                                        "TechCorp",
                                                        "INFO@techcorp.bo"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("BUS-" + existing.getId()))
                .andExpect(jsonPath("$.mensaje").value("Referido ya registrado previamente"));

        verify(referralRepository, never()).save(any());
    }

    @Test
    void claim_shouldRejectMissingRequiredFields() throws Exception {
        LinkedHashMap<String, Object> payload = new LinkedHashMap<>();
        payload.put("code", "");
        payload.put("nombre", "");

        mockMvc.perform(
                        post("/api/ambassadors/referrals/claim")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json(payload)))
                .andExpect(status().isBadRequest());
    }

    private AmbassadorReferralLinkJpaEntity referralLink(UUID ambassadorId, int conversions) {
        AmbassadorReferralLinkJpaEntity link = new AmbassadorReferralLinkJpaEntity();
        link.setId(UUID.randomUUID());
        link.setAmbassadorId(ambassadorId);
        link.setCode("EMBA-SCZ");
        link.setConversions(conversions);
        link.setActive(true);
        return link;
    }

    private AmbassadorJpaEntity ambassador(UUID id, String referralCode) {
        AmbassadorJpaEntity ambassador = new AmbassadorJpaEntity();
        ambassador.setId(id);
        ambassador.setReferralCode(referralCode);
        return ambassador;
    }

    private AmbassadorReferralJpaEntity existingReferral(String email) {
        AmbassadorReferralJpaEntity referral = new AmbassadorReferralJpaEntity();
        referral.setId(UUID.randomUUID());
        referral.setEmail(email);
        referral.setStatus("activo");
        return referral;
    }

    private LinkedHashMap<String, Object> claimPayload(String code, String nombre, String email) {
        LinkedHashMap<String, Object> payload = new LinkedHashMap<>();
        payload.put("code", code);
        payload.put("nombre", nombre);
        payload.put("email", email);
        payload.put("telefono", "+591 70000000");
        payload.put("ciudad", "Santa Cruz");
        payload.put("pais", "Bolivia");
        payload.put("tipo", "empresa");
        return payload;
    }

    private String json(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }
}
