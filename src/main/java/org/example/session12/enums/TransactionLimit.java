package org.example.session12.enums;

import java.math.BigDecimal;

public enum TransactionLimit {
    STANDARD_EKYC(new BigDecimal("100000000")),
    UNLIMITED(new BigDecimal("999999999999"));

    private final BigDecimal limit;

    TransactionLimit(BigDecimal limit) {
        this.limit = limit;
    }

    public BigDecimal getLimit() {
        return limit;
    }
}
