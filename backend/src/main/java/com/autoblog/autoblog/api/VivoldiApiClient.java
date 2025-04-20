package com.autoblog.autoblog.api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.autoblog.autoblog.dto.ApiKeyDto;

@Component
public class VivoldiApiClient {

    private final HttpClient client;

    public VivoldiApiClient() {
        this.client = HttpClient.newHttpClient();
    }

    public String getVivoldiLink(String url, ApiKeyDto apiKeyDto) {
        try {
            // 요청 본문 생성
            JSONObject params = new JSONObject();
            params.put("url", url);
            params.put("domain", "https://vvd.bz");

            // HTTP 요청 생성
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://vivoldi.com/api/link/v2/create"))
                .header("Content-Type", "application/json")
                .header("Authorization", "APIKey " + apiKeyDto.getVivoldiKey())
                .header("User-agent", apiKeyDto.getVivoldiId())
                .POST(HttpRequest.BodyPublishers.ofString(params.toString()))
                .build();

            // HTTP 요청 실행
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 응답 처리
            if (response.statusCode() == HttpStatus.OK.value()) {
                String jsonString = response.body();
                if (jsonString != null && !jsonString.isEmpty()) {
                    JSONObject json = new JSONObject(jsonString);
                    if (json.getInt("code") == 0) {
                        return json.getString("result"); // Short URL 반환
                    } else {
                        throw new RuntimeException(String.format("API Error [%d]: %s",
                                json.getInt("code"), json.getString("message")));
                    }
                } else {
                    throw new RuntimeException("Empty response body");
                }
            } else {
                throw new RuntimeException("HTTP Error: " + response.statusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to get Vivoldi link", e);
        }
    }
}
