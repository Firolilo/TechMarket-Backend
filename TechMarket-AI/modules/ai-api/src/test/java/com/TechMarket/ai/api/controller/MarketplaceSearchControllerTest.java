package com.techmarket.ai.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.techmarket.ai.application.dto.MarketplaceDocDto;
import com.techmarket.ai.application.dto.MarketplaceHitDto;
import com.techmarket.ai.application.port.in.MarketplaceSearchUseCase;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Verifies the marketplace semantic-search endpoints respond and forward query/docs to the use
 * case.
 */
@ExtendWith(MockitoExtension.class)
class MarketplaceSearchControllerTest {

    private MockMvc mvc;

    @Mock private MarketplaceSearchUseCase useCase;

    @BeforeEach
    void setUp() {
        mvc =
                MockMvcBuilders.standaloneSetup(new MarketplaceSearchController(useCase))
                        .setMessageConverters(new MappingJackson2HttpMessageConverter())
                        .build();
    }

    @Test
    void buscar_respondsAndReturnsHits() throws Exception {
        when(useCase.search(eq("alguien que repare placas y configure redes"), eq(5)))
                .thenReturn(
                        List.of(
                                new MarketplaceHitDto(
                                        "SVC-2", "especialista", "Redes", "Ana", 0.82)));

        mvc.perform(
                        post("/api/v1/ai/marketplace/buscar")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"query\":\"alguien que repare placas y configure redes\",\"topK\":5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("SVC-2"))
                .andExpect(jsonPath("$[0].type").value("especialista"));
    }

    @Test
    void index_respondsAndForwardsDocuments() throws Exception {
        when(useCase.reindex(org.mockito.ArgumentMatchers.anyList())).thenReturn(2);

        mvc.perform(
                        post("/api/v1/ai/marketplace/index")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        "{\"documentos\":[{\"id\":\"SVC-1\",\"type\":\"especialista\",\"title\":\"Reparacion\",\"ownerName\":\"Juan\",\"content\":\"reparo laptops\"}]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.indexed").value(2));

        ArgumentCaptor<List<MarketplaceDocDto>> captor = ArgumentCaptor.forClass(List.class);
        verify(useCase).reindex(captor.capture());
        assertThat(captor.getValue()).extracting(MarketplaceDocDto::id).containsExactly("SVC-1");
    }
}
