package com.autoblog.autoblog.repository;

import com.autoblog.autoblog.domain.DraftReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DraftReviewRepository extends JpaRepository<DraftReview, Long> {
}