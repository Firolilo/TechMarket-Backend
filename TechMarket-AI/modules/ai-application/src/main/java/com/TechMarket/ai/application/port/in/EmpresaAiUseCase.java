package com.techmarket.ai.application.port.in;

import com.techmarket.ai.application.dto.BusinessInsight;
import com.techmarket.ai.application.dto.EmpresaAiDtos;

/** Company-facing AI assistant: turns a business question + context into an actionable insight. */
public interface EmpresaAiUseCase {

    BusinessInsight consult(EmpresaAiDtos.Consult command);
}
