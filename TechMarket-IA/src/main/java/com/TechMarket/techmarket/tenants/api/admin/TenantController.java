package com.techmarket.techmarket.tenants.api.admin;

import com.techmarket.techmarket.tenants.api.admin.request.CreateTenantRequest;
import com.techmarket.techmarket.tenants.api.admin.response.TenantResponse;
import com.techmarket.techmarket.tenants.application.command.CreateTenantCommand;
import com.techmarket.techmarket.tenants.application.query.GetTenantByIdQuery;
import com.techmarket.techmarket.tenants.application.service.TenantApplicationService;
import com.techmarket.techmarket.tenants.domain.model.Tenant;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin/tenants")
public class TenantController {

    private final TenantApplicationService service;

    public TenantController(TenantApplicationService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TenantResponse create(@Valid @RequestBody CreateTenantRequest request) {
        Tenant tenant =
                service.create(
                        new CreateTenantCommand(
                                request.businessName(),
                                request.legalName(),
                                request.taxId(),
                                request.status()));
        return toResponse(tenant);
    }

    @GetMapping("/{id}")
    public TenantResponse getById(@PathVariable UUID id) {
        Tenant tenant = service.getById(new GetTenantByIdQuery(id));
        if (tenant == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenant not found");
        }
        return toResponse(tenant);
    }

    private TenantResponse toResponse(Tenant tenant) {
        return new TenantResponse(
                tenant.id(),
                tenant.businessName(),
                tenant.legalName(),
                tenant.taxId(),
                tenant.status(),
                tenant.createdAt(),
                tenant.updatedAt());
    }
}
