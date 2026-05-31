package com.edu.ifrn.AmazonScraper.dtos;

import com.edu.ifrn.AmazonScraper.entities.Product;

public record ProductDTO(Long id, String title, String url) {
    public static ProductDTO from(Product product) {
        return new ProductDTO(product.getId(), product.getTitle(), product.getUrl());
    }
}
