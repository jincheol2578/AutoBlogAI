package com.autoblog.autoblog.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.autoblog.autoblog.dto.ProductDto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ImageCrawlerApiClient {

    private static final Logger logger = LoggerFactory.getLogger(ImageCrawlerApiClient.class);
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ImageCrawlerApiClient() {
        this.restTemplate = new RestTemplate();
    }

    public List<String> getCrawlImagesResponse(String username, ProductDto productDto) {
        Map<String, String> requestBody = new HashMap<>();
        String url = productDto.getPartnersUrl();
        String keyword = productDto.getKeywordName();
        requestBody.put("url", url);
        requestBody.put("keyword", keyword);

        try {
            // Flask 서버에 POST 요청 보내기
            String response = restTemplate.postForObject("http://127.0.0.1:5000/crawl", requestBody, String.class);
            logger.info("Flask 서버 응답: {}", response);

            // JSON 파싱
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            // "status" 확인 후 "data" 추출
            if (root.has("status") && root.get("status").asText().equals("success") && root.has("data")) {
                JsonNode dataNode = root.get("data");
                List<String> imagePaths = new ArrayList<>();

                for (JsonNode pathNode : dataNode) {
                    imagePaths.add(pathNode.asText());
                }

                return imagePaths;
            } else {
                logger.warn("Flask 응답에 유효한 이미지 경로가 없습니다.");
                return List.of();
            }

        } catch (Exception e) {
            logger.error("이미지 크롤링 중 오류 발생: {}", e.getMessage(), e);
            return List.of();
        }
    }

}
