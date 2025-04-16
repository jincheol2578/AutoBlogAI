package com.autoblog.autoblog.api;

import java.io.IOException;

import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import com.autoblog.autoblog.util.HmacGenerator;

public class CoupangApiClient {
    private final String REQUEST_METHOD = "POST";
    private final String DOMAIN = "https://api-gateway.coupang.com";
    private final String URL = "/v2/providers/affiliate_open_api/apis/openapi/v1/deeplink";
    // Replace with your own ACCESS_KEY and SECRET_KEY
    private final String ACCESS_KEY = "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx";
    private final String SECRET_KEY = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";

    private final String REQUEST_JSON = "{\"coupangUrls\": [\"https://www.coupang.com/np/search?component=&q=good&channel=user\",\"https://www.coupang.com/np/coupangglobal\"]}";

    public void getCoupangProduct() throws IOException {
        // Generate HMAC string
        String authorization = HmacGenerator.generate(REQUEST_METHOD, URL, SECRET_KEY, ACCESS_KEY);

        // Send request
        StringEntity entity = new StringEntity(REQUEST_JSON, "UTF-8");
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");

        org.apache.http.HttpHost host = org.apache.http.HttpHost.create(DOMAIN);
        org.apache.http.HttpRequest request = org.apache.http.client.methods.RequestBuilder
                .post(URL).setEntity(entity)
                .addHeader("Authorization", authorization)
                .build();

        org.apache.http.HttpResponse httpResponse = org.apache.http.impl.client.HttpClientBuilder.create().build().execute(host, request);

        // verify
        System.out.println(EntityUtils.toString(httpResponse.getEntity()));
    }
}
