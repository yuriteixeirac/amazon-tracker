package com.edu.ifrn.AmazonScraper.services;

import com.edu.ifrn.AmazonScraper.dtos.ProductTrackMessageDTO;
import com.edu.ifrn.AmazonScraper.entities.Product;
import com.edu.ifrn.AmazonScraper.entities.User;
import com.edu.ifrn.AmazonScraper.repositories.ProductRepository;
import com.edu.ifrn.AmazonScraper.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RabbitMQService rabbitMQService;

    @InjectMocks
    private ProductService productService;

    @Test
    void trackProductCreatesProductAssociatesUserAndQueuesScraping() {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(productRepository.findByUrl("https://example.com/product")).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            product.setId(10L);
            return product;
        });

        Product product = productService.trackProduct("user@example.com", "https://example.com/product");

        assertThat(product.getId()).isEqualTo(10L);
        assertThat(user.getTrackedProducts()).contains(product);
        verify(userRepository).save(user);

        ArgumentCaptor<ProductTrackMessageDTO> messageCaptor = ArgumentCaptor.forClass(ProductTrackMessageDTO.class);
        verify(rabbitMQService).sendProductTrackMessage(messageCaptor.capture());
        assertThat(messageCaptor.getValue().productId()).isEqualTo(10L);
        assertThat(messageCaptor.getValue().url()).isEqualTo("https://example.com/product");
    }

    @Test
    void trackProductAssociatesExistingProductFromOwningSide() {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");

        Product product = new Product();
        product.setId(10L);
        product.setUrl("https://example.com/product");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(productRepository.findByUrl("https://example.com/product")).thenReturn(Optional.of(product));

        productService.trackProduct("user@example.com", "https://example.com/product");

        assertThat(user.getTrackedProducts()).contains(product);
        verify(userRepository).save(user);
    }
}
