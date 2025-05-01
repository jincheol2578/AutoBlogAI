package com.autoblog.autoblog.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Component
public class OpenRouterApiClient {

    private final WebClient webClient;

    public OpenRouterApiClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://127.0.0.1:5000").build(); // Python 서버의 URL 설정
    }

    public Mono<Map<String, Object>> getChatResponse(String productName, String keyword) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("product_name", productName);
        requestBody.put("keyword", keyword);
    
        return webClient.post()
                .uri("/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), 
                    response -> Mono.error(new RuntimeException("Failed to get response from Python API: " + response.statusCode()))
                )
                .bodyToMono(Map.class)
                .map(response -> {
                    if (response != null && response.containsKey("data")) {
                        return (Map<String, Object>) response.get("data");
                    } else {
                        throw new RuntimeException("Invalid response structure: 'data' key not found");
                    }
                });
    }
}