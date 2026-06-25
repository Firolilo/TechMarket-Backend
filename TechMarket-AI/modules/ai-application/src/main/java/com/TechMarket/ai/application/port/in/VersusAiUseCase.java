package com.techmarket.ai.application.port.in;

import com.techmarket.ai.application.dto.VersusAiDtos;
import com.techmarket.ai.application.dto.VersusVerdict;

/** Marketplace "Versus": turns two listings + context into an AI verdict on the better deal. */
public interface VersusAiUseCase {

    VersusVerdict compare(VersusAiDtos.Compare command);
}
