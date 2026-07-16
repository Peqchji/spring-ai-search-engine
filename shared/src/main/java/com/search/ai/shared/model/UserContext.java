package com.search.ai.shared.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserContext(
        String userId,
        GeoLocation location,
        List<String> preferences,
        List<String> recentSearches) {
}
