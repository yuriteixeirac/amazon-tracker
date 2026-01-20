package com.edu.ifrn.AmazonScraper.repositories;

import com.edu.ifrn.AmazonScraper.entities.ProductRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRecordRepository extends JpaRepository<ProductRecord, Long> {}
