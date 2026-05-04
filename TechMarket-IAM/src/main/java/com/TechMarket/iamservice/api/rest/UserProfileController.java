package com.techmarket.iamservice.api.rest;

import com.techmarket.iamservice.application.dto.PublicUserProfileResponse;
import com.techmarket.iamservice.application.dto.UpdateProfileRequest;
import com.techmarket.iamservice.application.dto.UserProfileResponse;
import com.techmarket.iamservice.application.service.UserProfileService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "User profiles", description = "Authenticated and public profile operations")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping("/users/profile")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<UserProfileResponse> profile(Authentication authentication) {
        return ResponseEntity.ok(userProfileService.currentProfile(authentication));
    }

    @PutMapping("/users/profile")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<UserProfileResponse> updateProfile(
            Authentication authentication, @Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userProfileService.updateCurrentProfile(authentication, request));
    }

    @GetMapping("/users/public/{userId}")
    public ResponseEntity<PublicUserProfileResponse> publicProfile(@PathVariable String userId) {
        return ResponseEntity.ok(userProfileService.publicProfile(userId));
    }
}
