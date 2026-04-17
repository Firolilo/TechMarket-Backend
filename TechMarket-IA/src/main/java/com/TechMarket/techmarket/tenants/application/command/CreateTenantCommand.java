package com.techmarket.techmarket.tenants.application.command;

public record CreateTenantCommand(
        String businessName, String legalName, String taxId, String status) {}
