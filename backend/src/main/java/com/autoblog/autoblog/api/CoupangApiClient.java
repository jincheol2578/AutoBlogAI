package com.autoblog.autoblog.api;

import java.io.IOException;
import java.net.URLEncoder;

import org.apache.http.NameValuePair;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import com.autoblog.autoblog.dto.CoupangApiDto;
import com.autoblog.autoblog.dto.ProductDto;
import com.autoblog.autoblog.util.HmacGenerator;

@Component
public class CoupangApiClient {
    private final String REQUEST_METHOD = "POST";
    private final String DOMAIN = "https://api-gateway.coupang.com";
    private final String URL = "v2/providers/affiliate_open_api/apis/openapi/products/search";
    // Replace with your own ACCESS_KEY and SECRET_KEY
    private final String ACCESS_KEY = "1102e410-2bc1-4448-82c2-9e43c6b13b22";
    private final String SECRET_KEY = "6049fee70afa30f7c523930d29e5cd01e4c84961";

    private final String REQUEST_JSON = "{\"coupangUrls\": [\"https://www.coupang.com/np/search?component=&q=good&channel=user\",\"https://www.coupang.com/np/coupangglobal\"]}";

    public String getCoupangProduct(CoupangApiDto dto) throws IOException {
        // Generate HMAC string
        String query = "?keyword=" + URLEncoder.encode(dto.getKeyword(), "UTF-8") + "&limit=10";
        String requestUrl = "/v2/providers/affiliate_open_api/apis/openapi/products/search" + query;

        String authorization = HmacGenerator.generate("GET", requestUrl, dto.getSecretKey(), dto.getApiKey());

        // Send request
        StringEntity entity = new StringEntity(REQUEST_JSON, "UTF-8");
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");

        org.apache.http.HttpHost host = org.apache.http.HttpHost.create(DOMAIN);
        org.apache.http.HttpRequest request = org.apache.http.client.methods.RequestBuilder
                .get(requestUrl).setEntity(entity)
                .addHeader("Authorization", authorization)
                .build();

        org.apache.http.HttpResponse httpResponse = org.apache.http.impl.client.HttpClientBuilder.create().build().execute(host, request);

        // verify
        System.out.println(EntityUtils.toString(httpResponse.getEntity()));

        return EntityUtils.toString(httpResponse.getEntity());
    }

    public String getPartnersLink(String url, CoupangApiDto dto) throws IOException {
        // Generate HMAC string
        String requestUrl = "/v2/providers/affiliate_open_api/apis/openapi/deeplink";

        String authorization = HmacGenerator.generate("POST", requestUrl, dto.getSecretKey(), dto.getApiKey());

        String requestJson = String.format(
        "{\"coupangUrls\": [\"%s\"], \"subId\": \"%s\"}",
        url, dto.getSubId()
    );

        // Send request
        StringEntity entity = new StringEntity(requestJson, "UTF-8");
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");

        org.apache.http.HttpHost host = org.apache.http.HttpHost.create(DOMAIN);
        org.apache.http.HttpRequest request = org.apache.http.client.methods.RequestBuilder
                .post(requestUrl).setEntity(entity)
                .addHeader("Authorization", authorization)
                .build();

        org.apache.http.HttpResponse httpResponse = org.apache.http.impl.client.HttpClientBuilder.create().build().execute(host, request);

        // verify
        String responseBody = EntityUtils.toString(httpResponse.getEntity());
        System.out.println(responseBody);

        return responseBody;
    }
}
