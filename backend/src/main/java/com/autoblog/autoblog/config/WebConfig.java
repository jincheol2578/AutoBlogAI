package com.autoblog.autoblog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("http://localhost:3000") // React dev 서버 주소  개발환경에선 *
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
                System.out.println("CORS Config Applied");
    }
}