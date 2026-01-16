package com.edu.ifrn.AmazonScraper.services;

import com.edu.ifrn.AmazonScraper.entities.ProductRecord;
import com.edu.ifrn.AmazonScraper.exceptions.EntityNotFoundException;
import com.edu.ifrn.AmazonScraper.repositories.ProductRecordRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductRecordService {
    private final ProductRecordRepository productRecordRepository;

    public ProductRecordService(ProductRecordRepository productRecordRepository) {
        this.productRecordRepository = productRecordRepository;
    }

    public ProductRecord findById(Long id) {
        return productRecordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Record not found."));
    }

    public List<ProductRecord> findAll() {
        return productRecordRepository.findAll();
    }

    public List<ProductRecord> findByProductId(Long id) {
        return productRecordRepository.findAll()
                .stream()
                .filter(record -> record
                        .getProduct().getId().equals(id)
                )
                .toList();
    }
}
