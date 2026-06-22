package com.techmarket.ai.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.techmarket.ai.application.dto.EmpresaAiDtos;
import com.techmarket.ai.application.dto.EspecialistaAiDtos;
import com.techmarket.ai.application.port.in.AmbassadorAiUseCase;
import com.techmarket.ai.application.port.in.EmpresaAiUseCase;
import com.techmarket.ai.application.port.in.EspecialistaAiUseCase;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Verifies that the role AI endpoints respond and forward the real-data {@code context} (assembled
 * from TechMarket-IA and sent by the frontend) down to the use case, so the LLM is grounded instead
 * of answering generically.
 */
@ExtendWith(MockitoExtension.class)
class RoleAiContextControllerTest {

    private MockMvc mvc;

    @Mock private EmpresaAiUseCase empresaUseCase;
    @Mock private EspecialistaAiUseCase especialistaUseCase;
    @Mock private AmbassadorAiUseCase ambassadorUseCase;

    @Captor private ArgumentCaptor<EmpresaAiDtos.Consult> consultCaptor;
    @Captor private ArgumentCaptor<Map<String, Object>> contextCaptor;

    @BeforeEach
    void setUp() {
        mvc =
                MockMvcBuilders.standaloneSetup(
                                new EmpresaAiController(empresaUseCase),
                                new EspecialistaAiController(especialistaUseCase),
                                new AmbassadorAiController(ambassadorUseCase))
                        .setMessageConverters(new MappingJackson2HttpMessageConverter())
                        .build();
    }

    @Test
    void empresaConsulta_forwardsRealContext() throws Exception {
        mvc.perform(
                        post("/api/empresa/ia/consulta")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"question\":\"¿Cómo mejoro?\",\"context\":{\"negocio\":{\"reputacion\":{\"calificacionPromedio\":4.3}}}}"))
                .andExpect(status().isOk());

        verify(empresaUseCase).consult(consultCaptor.capture());
        assertThat(consultCaptor.getValue().question()).isEqualTo("¿Cómo mejoro?");
        assertThat(consultCaptor.getValue().context()).containsKey("negocio");
    }

    @Test
    void especialistaInsights_respondsAndForwardsContext() throws Exception {
        mvc.perform(
                        post("/api/specialists/ai/insights")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"especialista\":{\"reputacion\":{\"totalResenas\":7}}}"))
                .andExpect(status().isOk());

        verify(especialistaUseCase).insights(contextCaptor.capture());
        assertThat(contextCaptor.getValue()).containsKey("especialista");
    }

    @Test
    void especialistaQuery_forwardsContextInDto() throws Exception {
        mvc.perform(
                        post("/api/specialists/ai/query")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"consulta\":\"¿Qué precio pongo?\",\"context\":{\"especialista\":{\"serviciosPublicados\":3}}}"))
                .andExpect(status().isOk());

        ArgumentCaptor<EspecialistaAiDtos.SpecialistQuery> queryCaptor =
                ArgumentCaptor.forClass(EspecialistaAiDtos.SpecialistQuery.class);
        verify(especialistaUseCase).query(queryCaptor.capture());
        assertThat(queryCaptor.getValue().context()).containsKey("especialista");
    }

    @Test
    void embajadorInsights_respondsAndForwardsContext() throws Exception {
        mvc.perform(
                        post("/api/ambassadors/ai/insights")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"embajador\":{\"referidos\":{\"activos\":2}}}"))
                .andExpect(status().isOk());

        verify(ambassadorUseCase).insights(contextCaptor.capture());
        assertThat(contextCaptor.getValue()).containsKey("embajador");
    }
}
