package com.autoblog.autoblog.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.autoblog.autoblog.api.CoupangApiClient;
import com.autoblog.autoblog.api.ImageCrawlerApiClient;
import com.autoblog.autoblog.api.VivoldiApiClient;
import com.autoblog.autoblog.controller.ProductController;
import com.autoblog.autoblog.domain.Keyword;
import com.autoblog.autoblog.domain.Product;
import com.autoblog.autoblog.domain.ProductImage;
import com.autoblog.autoblog.domain.User;
import com.autoblog.autoblog.dto.ApiKeyDto;
import com.autoblog.autoblog.dto.ProductDto;
import com.autoblog.autoblog.repository.KeywordRepository;
import com.autoblog.autoblog.repository.ProductImageRepository;
import com.autoblog.autoblog.repository.ProductRepository;
import com.autoblog.autoblog.repository.UserRepository;
import com.autoblog.autoblog.util.ProductParser;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final UserRepository userRepository;
    private final KeywordRepository keywordRepository;
    private final ProductRepository productRepository;
    private final CoupangApiClient coupangApiClient;
    private final VivoldiApiClient vivoldiApiClient;
    private final ProductImageRepository productImageRepository;
    private final ImageCrawlerApiClient imageCrawlerApiClient;

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    public List<ProductDto> getCoupangProducts(Long id, String username) throws IOException {
        logger.info("Fetching products for keyword ID: {} and username: {}", id, username);

        // 데이터베이스에서 사용자 정보 가져오기
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Keyword keyword = keywordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("키워드를 찾을 수 없습니다"));

        // 사용자 정보를 기반으로 CoupangApiDto 생성
        ApiKeyDto apiDto = new ApiKeyDto();
        apiDto.setApiKey(user.getCoupangApiKey());
        apiDto.setSecretKey(user.getCoupangSecretKey());
        apiDto.setKeyword(keyword.getKeyword());
        
        logger.debug("Calling Coupang API with API key: {}", user.getCoupangApiKey());
        
        // CoupangApiClient 호출
        String response = coupangApiClient.getCoupangProduct(apiDto);
        List<ProductDto> productList = ProductParser.parseProducts(keyword.getId(), response);

        logger.info("Retrieved {} products from Coupang for keyword {}", productList.size(), keyword.getKeyword());
        return productList;
    }

    public String addProduct(ProductDto productDto, String userName) {
        try {
            logger.info("Adding product for user: {}", userName);

            User user = userRepository.findByUsername(userName)
                    .orElseThrow(() -> new UsernameNotFoundException(userName));

            Keyword keyword = keywordRepository.findById(productDto.getKeywordId())
                    .orElseThrow(() -> new IllegalArgumentException("키워드를 찾을 수 없습니다."));

            ApiKeyDto apiKeyDto = new ApiKeyDto();
            apiKeyDto.setVivoldiId(user.getVivoldiId());
            apiKeyDto.setVivoldiKey(user.getVivoldiApiKey());

            logger.debug("Calling Vivoldi API to get partner URL for product: {}", productDto.getProductUrl());
            String partnersUrl = vivoldiApiClient.getVivoldiLink(productDto.getProductUrl(), apiKeyDto);

            Product product = Product.builder()
                    .keyword(keyword)
                    .productName(productDto.getProductName())
                    .price(productDto.getPrice())
                    .productImages(new ArrayList<>()) // TODO: 이미지 URL 처리 필요
                    .description(null) // TODO: 필요할까?
                    .productUrl(productDto.getProductUrl())
                    .partnerUrl(partnersUrl)
                    .build();

            logger.info("Saving product: {}", productDto.getProductName());
            productRepository.save(product);

            return "Product added successfully";
        } catch (IllegalArgumentException e) {
            logger.error("Failed to add product due to invalid argument: {}", e.getMessage());
            throw new RuntimeException("Failed to add product: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("An unexpected error occurred while adding the product: {}", e.getMessage());
            throw new RuntimeException("An unexpected error occurred while adding the product", e);
        }
    }

    public void addProductImages(String username, ProductDto productDto) {
        logger.info("Starting image crawling for product ID: {} and username: {}", productDto.getId(), username);
    
        // 1. 이미지 크롤링 호출 (동기 처리)
        List<String> imagePaths = imageCrawlerApiClient.getCrawlImagesResponse(username, productDto);
        logger.debug("Received {} images from image crawler", imagePaths.size());
    
        // 2. 이미지 크롤링 완료 후 저장 처리
        saveProductImages(imagePaths, productDto);
    }

    private void saveProductImages(List<String> imagePaths, ProductDto productDto) {
        logger.info("Saving images for product ID: {}", productDto.getId());
    
        // 3. 동기적으로 Product 객체 조회
        Optional<Product> productOpt = productRepository.findById(productDto.getId());
        if (!productOpt.isPresent()) {
            throw new IllegalArgumentException("상품 ID를 찾을 수 없습니다");
        }
        Product product = productOpt.get();
        logger.debug("Product found: {}", product.getProductName());
    
        // 4. 크롤링된 이미지 경로로 ProductImage 객체를 생성하여 DB에 동기적으로 저장
        List<ProductImage> productImages = new ArrayList<>();
        for (int i = 0; i < imagePaths.size(); i++) {
            String imagePath = imagePaths.get(i);
            ProductImage productImage = new ProductImage();
            productImage.setProduct(product);
            productImage.setImageUrl(imagePath);
            // 첫 번째 이미지는 isMain을 true로 설정
            if (i == 0) {
                productImage.setMain(true);
            }
            productImages.add(productImage);
        }
    
        // 5. 이미지 저장을 동기적으로 처리
        logger.debug("Saving {} images to database", productImages.size());
        productImageRepository.saveAll(productImages);
    }
}
