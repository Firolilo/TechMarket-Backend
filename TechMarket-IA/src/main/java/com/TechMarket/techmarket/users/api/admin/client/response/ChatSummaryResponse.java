package com.techmarket.techmarket.users.api.admin.client.response;

public record ChatSummaryResponse(
        String id, ChatCompanyResponse empresa, String ultimoMensaje, int mensajesSinLeer) {}
