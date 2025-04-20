package com.autoblog.autoblog.util;

import com.autoblog.autoblog.dto.ProductDto;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProductParser {

    public static List<ProductDto> parseProducts(String jsonResponse) {
        List<ProductDto> productList = new ArrayList<>();

        // JSON 응답 파싱
        JSONObject root = new JSONObject(jsonResponse);
        JSONArray productData = root.getJSONObject("data").getJSONArray("productData");

        for (int i = 0; i < productData.length(); i++) {
            JSONObject productJson = productData.getJSONObject(i);

            // ProductDto 객체 생성 및 데이터 설정
            ProductDto product = new ProductDto();
            product.setProductName(productJson.getString("productName"));
            product.setPrice(productJson.getLong("price"));
            product.setProductUrl(productJson.getString("productUrl"));

            // 리스트에 추가
            productList.add(product);
        }

        return productList;
    }
}
