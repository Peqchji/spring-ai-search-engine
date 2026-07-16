package com.search.ai.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class GlobalLoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();
        String method = exchange.getRequest().getMethod().name();
        
        long startTime = System.currentTimeMillis();
        
        return chain.filter(exchange)
                .then(
                    Mono.fromRunnable(() -> {
                        long endTime = System.currentTimeMillis();
                        long duration = endTime - startTime;
                        
                        int statusCode = 0;
                        HttpStatusCode code = exchange.getResponse().getStatusCode();
                        if (code != null) {
                            statusCode = code.value();
                        }
                        
                        log.info("API Edge: {} {} -> Status: {} [{}ms]", method, path, statusCode, duration);
                    }
                )
            );
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
