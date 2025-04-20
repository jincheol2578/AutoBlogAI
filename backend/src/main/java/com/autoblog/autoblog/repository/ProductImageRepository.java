package com.autoblog.autoblog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.autoblog.autoblog.domain.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findByProductId(Long productId);
}