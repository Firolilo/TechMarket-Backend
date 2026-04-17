package com.techmarket.techmarket.tickets.api.admin;

import com.techmarket.techmarket.tickets.api.admin.request.CreateTicketRequest;
import com.techmarket.techmarket.tickets.api.admin.response.TicketResponse;
import com.techmarket.techmarket.tickets.application.command.CreateTicketCommand;
import com.techmarket.techmarket.tickets.application.query.GetTicketByIdQuery;
import com.techmarket.techmarket.tickets.application.service.TicketApplicationService;
import com.techmarket.techmarket.tickets.domain.model.Ticket;
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
@RequestMapping("/api/admin/tickets")
public class TicketController {

    private final TicketApplicationService service;

    public TicketController(TicketApplicationService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TicketResponse create(@Valid @RequestBody CreateTicketRequest request) {
        Ticket ticket =
                service.create(
                        new CreateTicketCommand(
                                request.ticketCode(),
                                request.tenantId(),
                                request.customerUserId(),
                                request.listingId(),
                                request.subject(),
                                request.priority(),
                                request.status()));
        return toResponse(ticket);
    }

    @GetMapping("/{id}")
    public TicketResponse getById(@PathVariable UUID id) {
        Ticket ticket = service.getById(new GetTicketByIdQuery(id));
        if (ticket == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found");
        }
        return toResponse(ticket);
    }

    private TicketResponse toResponse(Ticket ticket) {
        return new TicketResponse(
                ticket.id(),
                ticket.ticketCode(),
                ticket.tenantId(),
                ticket.customerUserId(),
                ticket.listingId(),
                ticket.subject(),
                ticket.priority(),
                ticket.status(),
                ticket.openedAt(),
                ticket.createdAt());
    }
}
