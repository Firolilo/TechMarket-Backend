package com.techmarket.core.shared.exceptions;

import java.util.Collections;

public class TechMarketExceptionImpl extends TechMarketException {

    public TechMarketExceptionImpl(String code) {
        super(code, Collections.emptyMap(), null);
    }
}
