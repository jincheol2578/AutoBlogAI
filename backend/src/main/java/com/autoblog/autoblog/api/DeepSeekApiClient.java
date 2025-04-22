package com.autoblog.autoblog.api;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class DeepSeekApiClient {

    private final RestTemplate restTemplate;

    public DeepSeekApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, Object> getChatResponse(String productName, String keyword) {
        String url = "http://127.0.0.1:5000/chat"; // Python 서버의 URL

        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 본문 설정
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("product_name", productName);
        requestBody.put("keyword", keyword);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            // POST 요청 보내기
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);

            // 응답 처리
            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();

                // 중첩된 데이터 처리
                if (responseBody != null && responseBody.containsKey("data")) {
                    Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
                    return data; // "data" 키 아래의 데이터를 반환
                } else {
                    throw new RuntimeException("Invalid response structure: 'data' key not found");
                }
            } else {
                throw new RuntimeException("Failed to get response from Python API: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while calling Python API: " + e.getMessage(), e);
        }
    }
}
