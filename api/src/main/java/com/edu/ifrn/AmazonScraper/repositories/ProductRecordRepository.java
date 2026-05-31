package com.edu.ifrn.AmazonScraper.repositories;

import com.edu.ifrn.AmazonScraper.entities.ProductRecord;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRecordRepository extends JpaRepository<ProductRecord, Long> {
    @EntityGraph(attributePaths = "product")
    List<ProductRecord> findByProductTrackingUsersIdOrderByTrackedAtDesc(Long userId);

    @EntityGraph(attributePaths = "product")
    List<ProductRecord> findByProductIdAndProductTrackingUsersIdOrderByTrackedAtDesc(Long productId, Long userId);
}
