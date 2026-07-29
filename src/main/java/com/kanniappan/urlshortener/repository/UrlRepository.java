package com.kanniappan.urlshortener.repository;

import com.kanniappan.urlshortener.entity.Url;
import com.kanniappan.urlshortener.entity.User;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page; //
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {
    Optional<Url> findByShortCode(String ShortCode);

    Optional<Url> findByUserAndOriginalUrl(User user, String originalUrl);

    Page<Url> findByUserAndIsActiveTrue(User user, Pageable pageable);

    Optional<Url> findByShortCodeAndUser(String shortCode, User user);

    @Modifying
    @Query("""
            UPDATE Url u 
            SET u.clickCount = u.clickCount+1,
                u.updatedAt = :updatedAt
            WHERE u.shortCode = :shortCode
            """)
    int incrementClickCount(@Param("shortCode") String shortCode, @Param("updatedAt") LocalDateTime updatedAt);
}


