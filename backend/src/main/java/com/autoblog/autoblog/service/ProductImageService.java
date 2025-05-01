package com.autoblog.autoblog.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.autoblog.autoblog.domain.Product;
import com.autoblog.autoblog.domain.ProductImage;
import com.autoblog.autoblog.dto.ProductImageDto;
import com.autoblog.autoblog.repository.ProductImageRepository;
import com.autoblog.autoblog.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductImageService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;

}