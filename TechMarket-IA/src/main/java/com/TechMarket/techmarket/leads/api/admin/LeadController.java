package com.techmarket.techmarket.leads.api.admin;

import com.techmarket.techmarket.leads.api.admin.request.CreateLeadRequest;
import com.techmarket.techmarket.leads.api.admin.response.LeadResponse;
import com.techmarket.techmarket.leads.application.command.CreateLeadCommand;
import com.techmarket.techmarket.leads.application.query.GetLeadByIdQuery;
import com.techmarket.techmarket.leads.application.service.LeadApplicationService;
import com.techmarket.techmarket.leads.domain.model.Lead;
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
@RequestMapping("/api/admin/leads")
public class LeadController {

    private final LeadApplicationService service;

    public LeadController(LeadApplicationService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LeadResponse create(@Valid @RequestBody CreateLeadRequest request) {
        Lead lead =
                service.create(
                        new CreateLeadCommand(
                                request.tenantId(),
                                request.branchId(),
                                request.userId(),
                                request.listingId(),
                                request.sourceChannel(),
                                request.status(),
                                request.leadScore(),
                                request.notes()));
        return toResponse(lead);
    }

    @GetMapping("/{id}")
    public LeadResponse getById(@PathVariable UUID id) {
        Lead lead = service.getById(new GetLeadByIdQuery(id));
        if (lead == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lead not found");
        }
        return toResponse(lead);
    }

    private LeadResponse toResponse(Lead lead) {
        return new LeadResponse(
                lead.id(),
                lead.tenantId(),
                lead.branchId(),
                lead.userId(),
                lead.listingId(),
                lead.sourceChannel(),
                lead.status(),
                lead.leadScore(),
                lead.notes(),
                lead.createdAt(),
                lead.updatedAt());
    }
}
