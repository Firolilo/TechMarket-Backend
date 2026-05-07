package com.techmarket.techmarket.specialists.api.admin;

import com.techmarket.techmarket.specialists.api.admin.request.CreateSpecialistFileRequest;
import com.techmarket.techmarket.specialists.api.admin.response.CreateSpecialistFileResponse;
import com.techmarket.techmarket.specialists.api.admin.response.MessageResponse;
import com.techmarket.techmarket.specialists.api.admin.response.SpecialistFileResponse;
import com.techmarket.techmarket.specialists.application.service.SpecialistIdentitySupport;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.entity.SpecialistFileJpaEntity;
import com.techmarket.techmarket.specialists.infrastructure.persistence.jpa.repository.SpecialistFileSpringDataRepository;
import jakarta.validation.Valid;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/specialists/files")
public class SpecialistFileController {

    private final SpecialistIdentitySupport identitySupport;
    private final SpecialistFileSpringDataRepository fileRepository;

    public SpecialistFileController(
            SpecialistIdentitySupport identitySupport,
            SpecialistFileSpringDataRepository fileRepository) {
        this.identitySupport = identitySupport;
        this.fileRepository = fileRepository;
    }

    @GetMapping
    public List<SpecialistFileResponse> files(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        UUID currentUserId = identitySupport.requireUserId(userId);
        return fileRepository.findAllByUserIdOrderByCreatedAtDesc(currentUserId).stream()
                .map(this::toResponse)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateSpecialistFileResponse createFile(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @Valid @RequestBody CreateSpecialistFileRequest request) {
        UUID currentUserId = identitySupport.requireUserId(userId);
        OffsetDateTime now = OffsetDateTime.now();
        SpecialistFileJpaEntity file = new SpecialistFileJpaEntity();
        file.setId(UUID.randomUUID());
        file.setUserId(currentUserId);
        file.setFileUrl(request.url());
        file.setFileName(request.nombre());
        file.setFileType(request.tipo());
        file.setFileSize(request.tamano());
        file.setCreatedAt(now);
        file.setUpdatedAt(now);
        SpecialistFileJpaEntity saved = fileRepository.save(file);
        return new CreateSpecialistFileResponse(
                identitySupport.formatFileId(saved.getId()), "Archivo subido correctamente");
    }

    @DeleteMapping("/{fileId}")
    public MessageResponse deleteFile(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @PathVariable String fileId) {
        UUID currentUserId = identitySupport.requireUserId(userId);
        UUID id = identitySupport.parsePrefixedUuid(fileId, "FILE-");
        SpecialistFileJpaEntity file =
                fileRepository
                        .findByIdAndUserId(id, currentUserId)
                        .orElseThrow(
                                () ->
                                        new ResponseStatusException(
                                                HttpStatus.NOT_FOUND, "File not found"));
        fileRepository.delete(file);
        return new MessageResponse("Archivo eliminado");
    }

    private SpecialistFileResponse toResponse(SpecialistFileJpaEntity file) {
        OffsetDateTime createdAt = file.getCreatedAt();
        return new SpecialistFileResponse(
                identitySupport.formatFileId(file.getId()),
                file.getFileName(),
                file.getFileSize(),
                createdAt == null ? null : createdAt.toLocalDate().toString());
    }
}
