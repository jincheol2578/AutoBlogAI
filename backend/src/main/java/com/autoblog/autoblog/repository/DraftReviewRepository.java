package com.autoblog.autoblog.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.autoblog.autoblog.domain.DraftReview;

@Repository
public interface DraftReviewRepository extends JpaRepository<DraftReview, Long> {
    Optional<DraftReview> findByProductId(Long productId);
}