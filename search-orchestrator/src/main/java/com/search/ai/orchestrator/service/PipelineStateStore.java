package com.search.ai.orchestrator.service;

import com.search.ai.orchestrator.constants.PipelineConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PipelineStateStore {

    private final RedisTemplate<String, Object> redisTemplate;

    public void save(String correlationId, PipelineState state) {
        redisTemplate.opsForValue()
                .set(PipelineConstants.REDIS_STATE_KEY_PREFIX + correlationId, state, 10, TimeUnit.MINUTES);
    }

    public PipelineState get(String correlationId) {
        return (PipelineState) redisTemplate.opsForValue()
                .get(PipelineConstants.REDIS_STATE_KEY_PREFIX + correlationId);
    }

    public void delete(String correlationId) {
        redisTemplate.delete(PipelineConstants.REDIS_STATE_KEY_PREFIX + correlationId);
    }
}
