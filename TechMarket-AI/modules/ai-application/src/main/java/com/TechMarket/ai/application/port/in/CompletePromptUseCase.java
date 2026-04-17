package com.techmarket.ai.application.port.in;

import com.techmarket.ai.domain.model.Completion;
import com.techmarket.ai.domain.model.Prompt;

/** Port in: complete prompt use case. */
public interface CompletePromptUseCase {

    Completion complete(Prompt prompt);
}
