package com.techmarket.techmarket.specialists.api.admin.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateSpecialistPhotoRequest(
        @NotBlank(message = "url is required")
                @Size(max = 255, message = "url must have at most 255 chars")
                String url) {}
