package com.techmarket.iamservice.config.security;

import com.techmarket.iamservice.application.service.AccessTokenRevocationService;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class RevokedTokenValidator implements OAuth2TokenValidator<Jwt> {

    private static final OAuth2Error REVOKED_TOKEN_ERROR =
            new OAuth2Error("invalid_token", "Token has been revoked", null);

    private final AccessTokenRevocationService accessTokenRevocationService;

    public RevokedTokenValidator(AccessTokenRevocationService accessTokenRevocationService) {
        this.accessTokenRevocationService = accessTokenRevocationService;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        String tokenType = token.getClaimAsString("token_type");
        if (!"access".equals(tokenType)) {
            return OAuth2TokenValidatorResult.success();
        }

        String tokenId = token.getId();
        if (accessTokenRevocationService.isRevoked(tokenId)) {
            return OAuth2TokenValidatorResult.failure(REVOKED_TOKEN_ERROR);
        }

        return OAuth2TokenValidatorResult.success();
    }
}
