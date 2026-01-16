package com.edu.ifrn.AmazonScraper.repositories;

import com.edu.ifrn.AmazonScraper.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByUrl(String url);
    boolean existsByUrl(String url);
}
