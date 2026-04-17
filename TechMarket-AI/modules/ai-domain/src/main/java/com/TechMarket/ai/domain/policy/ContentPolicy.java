package com.techmarket.ai.domain.policy;

import com.techmarket.ai.domain.model.Prompt;

/** Domain policy: validations / rules without framework deps. */
public interface ContentPolicy {

    boolean allows(Prompt prompt);
}
