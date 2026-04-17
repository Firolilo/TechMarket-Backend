package com.techmarket.ai.application.port.out;

import com.techmarket.ai.domain.model.Completion;
import com.techmarket.ai.domain.model.Prompt;

/** Port out: LLM / completion provider. */
public interface LlmPort {

    Completion complete(Prompt prompt);
}
