package com.autoblog.autoblog.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.autoblog.autoblog.domain.Keyword;
import com.autoblog.autoblog.domain.User;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    boolean existsByKeywordAndUser(String keyword, User user);
    List<Keyword> findByUser(User user);
    public List<Keyword> findByKeywordInAndUser(String[] keywords, User user);
    
    @Query("SELECT DISTINCT k FROM Keyword k LEFT JOIN FETCH k.products WHERE k.user = :user")
    List<Keyword> findByUserWithProducts(@Param("user") User user);
}