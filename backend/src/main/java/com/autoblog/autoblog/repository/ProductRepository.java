package com.autoblog.autoblog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.autoblog.autoblog.domain.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}