package com.search.ai.shared.model;

import java.util.Map;

public record DocumentDetail(
                String id,
                String content,
                Map<String, Object> metadata) {
}
