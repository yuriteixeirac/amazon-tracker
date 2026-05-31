package com.edu.ifrn.AmazonScraper.services;

import com.edu.ifrn.AmazonScraper.dtos.ProductTrackMessageDTO;
import com.edu.ifrn.AmazonScraper.entities.Product;
import com.edu.ifrn.AmazonScraper.entities.User;
import com.edu.ifrn.AmazonScraper.exceptions.EntityNotFoundException;
import com.edu.ifrn.AmazonScraper.repositories.ProductRepository;
import com.edu.ifrn.AmazonScraper.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final RabbitMQService rabbitMQService;

    public ProductService(
            ProductRepository productRepository,
            UserRepository userRepository,
            RabbitMQService rabbitMQService
    ) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
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
    public Product trackProduct(String userEmail, String url) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));

        Product product = productRepository.findByUrl(url)
                .orElseGet(() -> productRepository.save(new Product(url)));

        user.getTrackedProducts().add(product);
        userRepository.save(user);

        rabbitMQService.sendProductTrackMessage(new ProductTrackMessageDTO(product.getId(), product.getUrl()));
        return product;
    }

    public Product findByIdForUser(Long id, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));

        return productRepository.findByIdAndTrackingUsersId(id, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found."));
    }

    public List<Product> findByTrackingUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));

        return productRepository.findByTrackingUsersId(user.getId());
    }
}
