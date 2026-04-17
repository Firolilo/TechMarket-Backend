package com.techmarket.ai.application.port.in;

import com.techmarket.ai.application.dto.RagQaCommandDto;
import com.techmarket.ai.application.dto.RagQaResultDto;

/** Port in: RAG QA use case (vector search + LLM with tenant/namespace filter). */
public interface RagQaUseCase {

    RagQaResultDto ask(RagQaCommandDto command);
}
