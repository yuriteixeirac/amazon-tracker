package com.edu.ifrn.AmazonScraper.services;

import com.edu.ifrn.AmazonScraper.dtos.UrlDTO;
import com.edu.ifrn.AmazonScraper.entities.Product;
import com.edu.ifrn.AmazonScraper.entities.User;
import com.edu.ifrn.AmazonScraper.exceptions.EntityAlreadyExistsException;
import com.edu.ifrn.AmazonScraper.exceptions.EntityNotFoundException;
import com.edu.ifrn.AmazonScraper.repositories.ProductRepository;
import com.edu.ifrn.AmazonScraper.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final RabbitMQService rabbitMQService;

    public ProductService(
            ProductRepository productRepository,
            RabbitMQService rabbitMQService
    ) {
        this.productRepository = productRepository;
        this.rabbitMQService = rabbitMQService;
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found."));
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Transactional
    public void save(User user, UrlDTO url) {
        Product existingProduct = productRepository
                .findByUrl(url.getUrl())
                .orElse(null);

        if (existingProduct != null) {
            existingProduct.getTrackingUsers().add(user);
            productRepository.save(existingProduct);

            return;
        }

        rabbitMQService.sendProductUrl("track_product", url);
    }

    public List<Product> findByTrackingUser(User user) {
        return productRepository.findAll()
                .stream()
                .filter(product -> product.getTrackingUsers().contains(user))
                .toList();
    }
}
