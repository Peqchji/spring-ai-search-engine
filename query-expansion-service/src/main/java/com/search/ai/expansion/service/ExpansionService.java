package com.search.ai.expansion.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.search.ai.shared.model.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpansionService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    private static final String EXPANSION_PROMPT = """
        You are a search assistant. Given a user query, produce 2 alternative
        search queries that capture the same intent using different wording.
        
        Rules:
        - Keep each variant concise (under 15 words)
        - Do not add new meaning not implied by the original
        - Use the user's recent searches and preferences to inform your variants if provided
        - Output ONLY a JSON array of strings, no explanation, no markdown formatting
        
        Original query: {query}
        User preferences: {preferences}
        Recent searches: {recentSearches}
        """;

    public List<String> expand(String query, UserContext userContext) {
        String preferences = userContext != null && userContext.preferences() != null 
                ? String.join(", ", userContext.preferences()) : "None";
        String recentSearches = userContext != null && userContext.recentSearches() != null 
                ? String.join(", ", userContext.recentSearches()) : "None";

        try {
            String response = chatClient.prompt()
                    .user(u -> u.text(EXPANSION_PROMPT)
                            .param("query", query)
                            .param("preferences", preferences)
                            .param("recentSearches", recentSearches))
                    .call()
                    .content();

            if (StringUtils.hasText(response)) {
                String cleanResponse = response.replaceAll("```json", "").replaceAll("```", "").trim();
                return objectMapper.readValue(cleanResponse, new TypeReference<List<String>>() {});
            }
        } catch (Exception e) {
            log.error("Failed to expand query: {}", query, e);
        }
        
        return List.of(query);
    }
}
