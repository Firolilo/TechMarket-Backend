package com.techmarket.core.shared.exceptions;

import java.util.Map;

public abstract class ApplicationException extends TechMarketException {

    protected ApplicationException(String code, Map<String, Object> args, String technicalMessage) {
        super(code, args, technicalMessage);
    }
}
