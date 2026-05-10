package com.techmarket.techmarket.ambassadors.api.admin.response;

public record AmbassadorCommissionSummaryResponse(
        String totalGenerado, String disponible, String pendiente, String pagado) {}
