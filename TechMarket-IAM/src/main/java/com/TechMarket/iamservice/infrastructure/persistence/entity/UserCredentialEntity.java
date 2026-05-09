package com.techmarket.iamservice.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "iam_user_credential",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_iam_user_credential_user",
                    columnNames = {"user_id"}),
            @UniqueConstraint(
                    name = "uk_iam_user_credential_tenant_user",
                    columnNames = {"tenant_id", "user_id"})
        })
public class UserCredentialEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "password_hash", nullable = false, length = 120)
    private String passwordHash;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_modified_at")
    private LocalDateTime lastModifiedAt;

    @Column(name = "otp_enabled", nullable = false)
    private boolean otpEnabled;

    @Column(name = "otp_code_hash", length = 255)
    private String otpCodeHash;

    @Column(name = "otp_challenge_id", length = 120)
    private String otpChallengeId;

    @Column(name = "otp_expires_at")
    private LocalDateTime otpExpiresAt;

    @Column(name = "otp_attempts", nullable = false)
    private int otpAttempts;

    protected UserCredentialEntity() {}

    public UserCredentialEntity(Long userId, String tenantId, String passwordHash) {
        this.userId = userId;
        this.tenantId = tenantId;
        this.passwordHash = passwordHash;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public boolean isOtpEnabled() {
        return otpEnabled;
    }

    public void setOtpEnabled(boolean otpEnabled) {
        this.otpEnabled = otpEnabled;
    }

    public String getOtpCodeHash() {
        return otpCodeHash;
    }

    public String getOtpChallengeId() {
        return otpChallengeId;
    }

    public LocalDateTime getOtpExpiresAt() {
        return otpExpiresAt;
    }

    public int getOtpAttempts() {
        return otpAttempts;
    }

    public void beginOtpChallenge(
            String otpCodeHash, String otpChallengeId, LocalDateTime otpExpiresAt) {
        this.otpCodeHash = otpCodeHash;
        this.otpChallengeId = otpChallengeId;
        this.otpExpiresAt = otpExpiresAt;
        this.otpAttempts = 0;
    }

    public void registerOtpAttempt() {
        this.otpAttempts += 1;
    }

    public void clearOtpChallenge() {
        this.otpCodeHash = null;
        this.otpChallengeId = null;
        this.otpExpiresAt = null;
        this.otpAttempts = 0;
    }

    @jakarta.persistence.PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @jakarta.persistence.PreUpdate
    protected void onUpdate() {
        this.lastModifiedAt = LocalDateTime.now();
    }
}
