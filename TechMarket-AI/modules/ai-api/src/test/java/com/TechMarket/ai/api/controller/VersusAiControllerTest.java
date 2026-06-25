package com.techmarket.ai.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.techmarket.ai.application.dto.VersusAiDtos;
import com.techmarket.ai.application.port.in.VersusAiUseCase;
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
 * Verifies the Versus endpoint responds and forwards BOTH listings (plus the real-data context)
 * down to the use case so the LLM can decide the better deal grounded in actual data.
 */
@ExtendWith(MockitoExtension.class)
class VersusAiControllerTest {

    private MockMvc mvc;

    @Mock private VersusAiUseCase useCase;
    @Captor private ArgumentCaptor<VersusAiDtos.Compare> compareCaptor;

    @BeforeEach
    void setUp() {
        mvc =
                MockMvcBuilders.standaloneSetup(new VersusAiController(useCase))
                        .setMessageConverters(new MappingJackson2HttpMessageConverter())
                        .build();
    }

    @Test
    void versus_forwardsBothProductsAndContext() throws Exception {
        mvc.perform(
                        post("/api/marketplace/versus")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"productos\":["
                                                + "{\"id\":\"p1\",\"nombre\":\"Laptop A\",\"precio\":5000,\"calificacion\":4.5,\"totalResenas\":12,\"reputacionVendedor\":4.6},"
                                                + "{\"id\":\"p2\",\"nombre\":\"Laptop B\",\"precio\":4200,\"calificacion\":4.1,\"totalResenas\":3,\"reputacionVendedor\":3.9}"
                                                + "],\"context\":{\"fuente\":\"versus-contexto\"}}"))
                .andExpect(status().isOk());

        verify(useCase).compare(compareCaptor.capture());
        VersusAiDtos.Compare command = compareCaptor.getValue();
        assertThat(command.productos()).hasSize(2);
        assertThat(command.productos().get(0).id()).isEqualTo("p1");
        assertThat(command.productos().get(1).id()).isEqualTo("p2");
        assertThat(command.context()).containsKey("fuente");
    }
}
