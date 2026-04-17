package com.techmarket.core.shared.exceptions;

import java.util.Map;

public abstract class DomainException extends TechMarketException {

    protected DomainException(String code, Map<String, Object> args, String technicalMessage) {
        super(code, args, technicalMessage);
    }
}
