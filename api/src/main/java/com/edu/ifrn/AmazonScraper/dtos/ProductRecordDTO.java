package com.edu.ifrn.AmazonScraper.dtos;

import com.edu.ifrn.AmazonScraper.entities.ProductRecord;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductRecordDTO(Long id, Long productId, BigDecimal price, LocalDateTime trackedAt) {
    public static ProductRecordDTO from(ProductRecord record) {
        return new ProductRecordDTO(
                record.getId(),
                record.getProduct().getId(),
                record.getPrice(),
                record.getTrackedAt()
        );
    }
}
