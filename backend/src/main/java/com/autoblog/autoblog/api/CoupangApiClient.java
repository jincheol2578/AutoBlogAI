package com.autoblog.autoblog.api;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import com.autoblog.autoblog.dto.ApiKeyDto;
import com.autoblog.autoblog.util.HmacGenerator;

@Component
public class CoupangApiClient {
    private final String DOMAIN = "https://api-gateway.coupang.com";

    public String getCoupangProduct(ApiKeyDto dto) throws IOException {
        // Generate HMAC string
        String query = "?keyword=" + URLEncoder.encode(dto.getKeyword(), "UTF-8") + "&limit=10";
        String requestUrl = "/v2/providers/affiliate_open_api/apis/openapi/products/search" + query;

        String authorization = HmacGenerator.generate("GET", requestUrl, dto.getSecretKey(), dto.getApiKey());

        // Send request
        StringEntity entity = new StringEntity( requestUrl, "UTF-8");
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");

        org.apache.http.HttpHost host = org.apache.http.HttpHost.create(DOMAIN);
        org.apache.http.HttpRequest request = org.apache.http.client.methods.RequestBuilder
                .get(requestUrl).setEntity(entity)
                .addHeader("Authorization", authorization)
                .build();

        org.apache.http.HttpResponse httpResponse = org.apache.http.impl.client.HttpClientBuilder.create().build().execute(host, request);

        try (InputStream inputStream = httpResponse.getEntity().getContent()) {
            String responseBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return responseBody;
        }
    }


    // 이미 상품 조회시 자동 변환이 되기 때문에 필요없음
    // public String getPartnersLink(String url, ApiKeyDto dto) throws IOException {
    //     // Generate HMAC string
    //     String requestUrl = "/v2/providers/affiliate_open_api/apis/openapi/deeplink";

    //     String authorization = HmacGenerator.generate("POST", requestUrl, dto.getSecretKey(), dto.getApiKey());

    //     String requestJson = String.format(
    //     "{\"coupangUrls\": [\"%s\"], \"subId\": \"%s\"}",
    //     url, dto.getSubId()
    // );

    //     // Send request
    //     StringEntity entity = new StringEntity(requestJson, "UTF-8");
    //     entity.setContentEncoding("UTF-8");
    //     entity.setContentType("application/json");

    //     org.apache.http.HttpHost host = org.apache.http.HttpHost.create(DOMAIN);
    //     org.apache.http.HttpRequest request = org.apache.http.client.methods.RequestBuilder
    //             .post(requestUrl).setEntity(entity)
    //             .addHeader("Authorization", authorization)
    //             .build();

    //     org.apache.http.HttpResponse httpResponse = org.apache.http.impl.client.HttpClientBuilder.create().build().execute(host, request);

    //     // verify
    //     String responseBody = EntityUtils.toString(httpResponse.getEntity());
    //     System.out.println(responseBody);

    //     return responseBody;
    // }
}
