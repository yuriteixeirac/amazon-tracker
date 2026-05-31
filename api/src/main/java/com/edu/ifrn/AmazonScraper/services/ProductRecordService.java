package com.edu.ifrn.AmazonScraper.services;

import com.edu.ifrn.AmazonScraper.entities.ProductRecord;
import com.edu.ifrn.AmazonScraper.exceptions.EntityNotFoundException;
import com.edu.ifrn.AmazonScraper.repositories.ProductRecordRepository;
import com.edu.ifrn.AmazonScraper.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductRecordService {
    private final ProductRecordRepository productRecordRepository;
    private final UserRepository userRepository;

    public ProductRecordService(
            ProductRecordRepository productRecordRepository,
            UserRepository userRepository
    ) {
        this.productRecordRepository = productRecordRepository;
        this.userRepository = userRepository;
    }

    public ProductRecord findById(Long id) {
        return productRecordRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Record not found."));
    }

    public List<ProductRecord> findAll() {
        return productRecordRepository.findAll();
    }

    public List<ProductRecord> findByUser(String userEmail) {
        Long userId = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found."))
                .getId();

        return productRecordRepository.findByProductTrackingUsersIdOrderByTrackedAtDesc(userId);
    }

    public List<ProductRecord> findByProductIdForUser(Long productId, String userEmail) {
        Long userId = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found."))
                .getId();

        return productRecordRepository.findByProductIdAndProductTrackingUsersIdOrderByTrackedAtDesc(productId, userId);
    }
}
