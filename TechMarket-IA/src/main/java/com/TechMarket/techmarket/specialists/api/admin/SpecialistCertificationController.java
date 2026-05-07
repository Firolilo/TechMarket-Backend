package com.techmarket.techmarket.specialists.api.admin;

import com.techmarket.techmarket.specialists.api.admin.request.CreateSpecialistCertificationRequest;
import com.techmarket.techmarket.specialists.api.admin.response.CreateSpecialistCertificationResponse;
import com.techmarket.techmarket.specialists.api.admin.response.MessageResponse;
import com.techmarket.techmarket.specialists.api.admin.response.SpecialistCertificationResponse;
import com.techmarket.techmarket.specialists.api.admin.response.SpecialistCertificationVerifyResponse;
import com.techmarket.techmarket.specialists.application.service.SpecialistIdentitySupport;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.entity.SpecialistCertificationJpaEntity;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistCertificationSpringDataRepository;
import jakarta.validation.Valid;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/specialists/certifications")
public class SpecialistCertificationController {

    private final SpecialistIdentitySupport identitySupport;
    private final SpecialistCertificationSpringDataRepository certificationRepository;

    public SpecialistCertificationController(
            SpecialistIdentitySupport identitySupport,
            SpecialistCertificationSpringDataRepository certificationRepository) {
        this.identitySupport = identitySupport;
        this.certificationRepository = certificationRepository;
    }

    @GetMapping
    public List<SpecialistCertificationResponse> certifications(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        UUID currentUserId = identitySupport.requireUserId(userId);
        return certificationRepository.findAllByUserIdOrderByCreatedAtDesc(currentUserId).stream()
                .map(this::toResponse)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateSpecialistCertificationResponse create(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody CreateSpecialistCertificationRequest request) {
        UUID currentUserId = identitySupport.requireUserId(userId);
        OffsetDateTime now = OffsetDateTime.now();
        SpecialistCertificationJpaEntity certification = new SpecialistCertificationJpaEntity();
        certification.setId(UUID.randomUUID());
        certification.setUserId(currentUserId);
        certification.setName(request.nombre());
        certification.setInstitution(request.institucion());
        certification.setObtainedAt(request.fechaObtencion());
        certification.setFileUrl(request.archivoUrl());
        certification.setStatus("pendiente");
        certification.setCreatedAt(now);
        certification.setUpdatedAt(now);
        SpecialistCertificationJpaEntity saved = certificationRepository.save(certification);
        return new CreateSpecialistCertificationResponse(
                identitySupport.formatCertificationId(saved.getId()), "Certificación agregada");
    }

    @DeleteMapping("/{certId}")
    public MessageResponse delete(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String certId) {
        UUID currentUserId = identitySupport.requireUserId(userId);
        SpecialistCertificationJpaEntity certification = findCertification(certId, currentUserId);
        certificationRepository.delete(certification);
        return new MessageResponse("Certificación eliminada");
    }

    @PatchMapping("/{certId}/verify")
    @Transactional
    public SpecialistCertificationVerifyResponse verify(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String certId) {
        UUID currentUserId = identitySupport.requireUserId(userId);
        SpecialistCertificationJpaEntity certification = findCertification(certId, currentUserId);
        certification.setStatus("en_verificacion");
        certification.setUpdatedAt(OffsetDateTime.now());
        certificationRepository.save(certification);
        return new SpecialistCertificationVerifyResponse(
                "en_verificacion", "Certificación enviada para verificación");
    }

    private SpecialistCertificationJpaEntity findCertification(String certId, UUID currentUserId) {
        UUID id = identitySupport.parsePrefixedUuid(certId, "CERT-");
        return certificationRepository
                .findByIdAndUserId(id, currentUserId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Certification not found"));
    }

    private SpecialistCertificationResponse toResponse(SpecialistCertificationJpaEntity certification) {
        return new SpecialistCertificationResponse(
                identitySupport.formatCertificationId(certification.getId()),
                certification.getName(),
                certification.getInstitution(),
                certification.getObtainedAt(),
                certification.getFileUrl());
    }
}
