package com.search.ai.shared.util;

import java.util.UUID;

public final class CorrelationIdGenerator {

    private CorrelationIdGenerator() {}

    public static String generate() {
        return UUID.randomUUID().toString();
    }
}
