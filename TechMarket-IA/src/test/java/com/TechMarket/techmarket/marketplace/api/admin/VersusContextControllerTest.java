package com.techmarket.techmarket.marketplace.api.admin;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.techmarket.techmarket.listings.infrastructure.persistence.jpa.entity.ListingJpaEntity;
import com.techmarket.techmarket.listings.infrastructure.persistence.jpa.repository.ListingImageSpringDataRepository;
import com.techmarket.techmarket.listings.infrastructure.persistence.jpa.repository.ListingSpringDataRepository;
import com.techmarket.techmarket.marketplace.infrastructure.persistence.jpa.repository.CatalogCategorySpringDataRepository;
import com.techmarket.techmarket.security.jwt.JwtTokenProvider;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistProfileSpringDataRepository;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistServiceSpringDataRepository;
import com.techmarket.techmarket.tenants.infrastructure.persistence.jpa.entity.TenantJpaEntity;
import com.techmarket.techmarket.tenants.infrastructure.persistence.jpa.repository.TenantSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.entity.ClientReviewJpaEntity;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.ClientReviewSpringDataRepository;
import com.techmarket.techmarket.users.infrastructure.persistence.jpa.repository.UserSpringDataRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MarketplaceController.class)
@AutoConfigureMockMvc(addFilters = false)
class VersusContextControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private ListingSpringDataRepository listingRepository;
    @MockBean private ListingImageSpringDataRepository listingImageRepository;
    @MockBean private CatalogCategorySpringDataRepository categoryRepository;
    @MockBean private TenantSpringDataRepository tenantRepository;
    @MockBean private ClientReviewSpringDataRepository reviewRepository;
    @MockBean private SpecialistServiceSpringDataRepository specialistServiceRepository;
    @MockBean private SpecialistProfileSpringDataRepository specialistProfileRepository;
    @MockBean private UserSpringDataRepository userRepository;

    // @WebMvcTest registra los filtros servlet; este mock permite construirlos.
    @MockBean private JwtTokenProvider jwtTokenProvider;

    @Test
    void versusContext_returnsRealDataPerProduct() throws Exception {
        UUID listingId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();

        ListingJpaEntity listing = mock(ListingJpaEntity.class);
        when(listing.getId()).thenReturn(listingId);
        when(listing.getTenantId()).thenReturn(tenantId);
        when(listing.getTitle()).thenReturn("Laptop Pro 14");
        when(listing.getBasePrice()).thenReturn(new BigDecimal("5000"));
        when(listing.getDescription()).thenReturn("Ultraligera para trabajo");
        when(listingRepository.findById(listingId)).thenReturn(Optional.of(listing));

        ClientReviewJpaEntity review = mock(ClientReviewJpaEntity.class);
        when(review.getRating()).thenReturn(new BigDecimal("4.5"));
        when(reviewRepository.findAllByListingIdOrderByCreatedAtDesc(listingId))
                .thenReturn(List.of(review));
        when(reviewRepository.findAllByTenantId(tenantId)).thenReturn(List.of(review));

        TenantJpaEntity tenant = mock(TenantJpaEntity.class);
        when(tenant.getId()).thenReturn(tenantId);
        when(tenant.getBusinessName()).thenReturn("Andes Tech Store");
        when(tenantRepository.findById(tenantId)).thenReturn(Optional.of(tenant));

        String payload = "{\"productIds\":[\"PROD-" + listingId + "\"]}";

        mockMvc.perform(
                        post("/api/marketplace/versus-contexto")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productos[0].id").value("PROD-" + listingId))
                .andExpect(jsonPath("$.productos[0].nombre").value("Laptop Pro 14"))
                .andExpect(jsonPath("$.productos[0].calificacion").value(4.5))
                .andExpect(jsonPath("$.productos[0].totalResenas").value(1))
                .andExpect(jsonPath("$.productos[0].reputacionVendedor").value(4.5))
                .andExpect(jsonPath("$.productos[0].empresa").value("Andes Tech Store"));
    }
}
