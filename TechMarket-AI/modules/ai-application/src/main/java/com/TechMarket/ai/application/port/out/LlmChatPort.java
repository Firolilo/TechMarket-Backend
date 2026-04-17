package com.techmarket.ai.application.port.out;

import com.techmarket.ai.application.dto.ChatResultDto;

/** Port out: LLM chat (prompt in, generic ChatResult with answer + usage out). */
public interface LlmChatPort {

    ChatResultDto chat(String prompt);
}
