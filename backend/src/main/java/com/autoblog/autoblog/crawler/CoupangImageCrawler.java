package com.autoblog.autoblog.crawler;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.imageio.ImageIO;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import com.autoblog.autoblog.util.ImageTransformer;

import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;

@Component
public class CoupangImageCrawler {

    private final ImageTransformer imageTransformer;

    public CoupangImageCrawler(ImageTransformer imageTransformer) {
        this.imageTransformer = imageTransformer;
    }

    public List<String> fetchAndTransformImages(String url, File outputDir) {
        List<String> savedImagePaths = new ArrayList<>();

        // ChromeDriver 자동 설정
        WebDriverManager.chromedriver().setup();

        // ChromeOptions 설정
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-software-rasterizer");
        options.addArguments("--disable-features=SharedArrayBuffer");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36");
        options.addArguments("--disable-blink-features=AutomationControlled");

        WebDriver driver = new ChromeDriver(options);

        try {
            // 1. 쿠팡 홈으로 접속
            driver.get("https://www.coupang.com/");
            randomDelayAsync().join(); // 비동기 딜레이

            // 2. 원하는 URL로 이동
            driver.get(url);
            randomDelayAsync().join(); // 비동기 딜레이

            // WebDriverWait 설정 (최대 10초 대기)
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // 3. .prod-image__detail 이미지 가져오기
            List<WebElement> mainImages = driver.findElements(By.cssSelector(".prod-image__item"));
            for (WebElement imageElement : mainImages) {
                if (savedImagePaths.size() >= 7) break; // 총 7장 채우면 종료
                imageElement.click();
                randomDelayAsync().join(); // 비동기 딜레이

                WebElement detailImageElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".prod-image__detail img")));
                String detailImageUrl = detailImageElement.getAttribute("src");
                if (!detailImageUrl.isEmpty()) {
                    BufferedImage originalImage = fetchImage(detailImageUrl);
                    String savedPath = imageTransformer.transformImage(originalImage, outputDir);
                    savedImagePaths.add(savedPath);
                }
            }

            // 4. .product-detail-content img에서 부족한 이미지 채우기
            if (savedImagePaths.size() < 7) {
                List<WebElement> detailImages = driver.findElements(By.cssSelector(".product-detail-content img"));
                for (WebElement imgElement : detailImages) {
                    if (savedImagePaths.size() >= 7) break; // 총 7장 채우면 종료
                    String imageUrl = imgElement.getAttribute("src");
                    if (!imageUrl.isEmpty()) {
                        BufferedImage originalImage = fetchImage(imageUrl);
                        String savedPath = imageTransformer.transformImage(originalImage, outputDir);
                        savedImagePaths.add(savedPath);
                    }
                }
            }

        } catch (org.openqa.selenium.UnhandledAlertException e) {
            // Alert 창 처리
            try {
                Alert alert = driver.switchTo().alert();
                System.out.println("Alert detected: " + alert.getText());
                alert.accept(); // 확인 버튼 클릭
                System.out.println("Alert accepted. Continuing...");
            } catch (Exception alertException) {
                System.err.println("Error handling alert: " + alertException.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Error fetching or transforming images: " + e.getMessage());
        } finally {
            // driver.quit();
        }

        return savedImagePaths;
    }

    private BufferedImage fetchImage(String imageUrl) {
        try (InputStream inputStream = new URL(imageUrl).openStream()) {
            return ImageIO.read(inputStream);
        } catch (Exception e) {
            System.err.println("Error fetching image: " + e.getMessage());
            return null;
        }
    }

    private CompletableFuture<Void> randomDelayAsync() {
        return CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep((long) (Math.random() * 2000) + 1000); // 1초에서 3초 사이의 랜덤 딜레이
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }
}
