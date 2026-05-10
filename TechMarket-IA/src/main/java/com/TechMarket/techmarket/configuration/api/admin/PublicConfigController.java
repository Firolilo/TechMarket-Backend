package com.techmarket.techmarket.configuration.api.admin;

import com.techmarket.techmarket.marketplace.infrastructure.persistence.jpa.entity.CatalogCategoryJpaEntity;
import com.techmarket.techmarket.marketplace.infrastructure.persistence.jpa.repository.CatalogCategorySpringDataRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/config")
public class PublicConfigController {

    private final CatalogCategorySpringDataRepository categoryRepository;

    public PublicConfigController(CatalogCategorySpringDataRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/countries")
    public List<CountryResponse> countries() {
        return List.of(new CountryResponse("BO", "Bolivia", "+591", "BOB"));
    }

    @GetMapping("/countries/{countryCode}/cities")
    public List<CityResponse> cities(@PathVariable String countryCode) {
        if (!"BO".equalsIgnoreCase(countryCode)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Country not found");
        }
        return List.of(
                new CityResponse("CITY-SCZ", "Santa Cruz"),
                new CityResponse("CITY-LPZ", "La Paz"),
                new CityResponse("CITY-CBB", "Cochabamba"));
    }

    @GetMapping("/currencies")
    public List<CurrencyResponse> currencies() {
        return List.of(
                new CurrencyResponse("BOB", "Boliviano", "Bs"),
                new CurrencyResponse("USD", "Dolar estadounidense", "$"));
    }

    @GetMapping("/categories")
    public List<CategoryResponse> categories() {
        return categoryRepository.findAllByParentCategoryIdIsNull().stream()
                .map(this::toCategoryResponse)
                .toList();
    }

    @GetMapping("/user-types")
    public List<UserTypeResponse> userTypes() {
        return List.of(
                new UserTypeResponse("cliente", "Cliente"),
                new UserTypeResponse("empresa", "Empresa"),
                new UserTypeResponse("especialista", "Especialista"),
                new UserTypeResponse("embajador", "Embajador"),
                new UserTypeResponse("admin", "Administrador"));
    }

    @GetMapping("/platform")
    public PlatformConfigResponse platform() {
        return new PlatformConfigResponse("TechMarket", "BO", "BOB", "soporte@techmarket.bo");
    }

    @GetMapping("/payment-options")
    public List<PaymentOptionResponse> paymentOptions() {
        return List.of(
                new PaymentOptionResponse("tarjeta", "Tarjeta de debito/credito"),
                new PaymentOptionResponse("qr", "Pago QR"),
                new PaymentOptionResponse("transferencia", "Transferencia bancaria"));
    }

    private CategoryResponse toCategoryResponse(CatalogCategoryJpaEntity category) {
        return new CategoryResponse(
                formatCategoryId(category.getId()),
                category.getName(),
                categoryRepository.findAllByParentCategoryId(category.getId()).stream()
                        .map(child -> new CategoryChildResponse(formatCategoryId(child.getId()), child.getName()))
                        .toList());
    }

    private String formatCategoryId(UUID id) {
        return "CAT-" + id;
    }

    public record CountryResponse(
            String codigo, String nombre, String telefonoPrefijo, String monedaDefault) {}

    public record CityResponse(String id, String nombre) {}

    public record CurrencyResponse(String codigo, String nombre, String simbolo) {}

    public record CategoryResponse(
            String id, String nombre, List<CategoryChildResponse> subcategorias) {}

    public record CategoryChildResponse(String id, String nombre) {}

    public record UserTypeResponse(String id, String nombre) {}

    public record PlatformConfigResponse(
            String nombre, String paisDefault, String monedaDefault, String soporteEmail) {}

    public record PaymentOptionResponse(String id, String nombre) {}
}
