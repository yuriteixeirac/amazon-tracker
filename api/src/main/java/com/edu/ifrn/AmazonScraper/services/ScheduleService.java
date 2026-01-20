package com.edu.ifrn.AmazonScraper.services;

import com.edu.ifrn.AmazonScraper.dtos.UrlDTO;
import com.edu.ifrn.AmazonScraper.repositories.ProductRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleService {

    private final ProductRepository productRepository;
    private final RabbitMQService rabbitMQService;

    public ScheduleService(
            ProductRepository productRepository,
            RabbitMQService rabbitMQService
    ) {
        this.productRepository = productRepository;
        this.rabbitMQService = rabbitMQService;
    }

    @Scheduled(fixedDelay = 1_800_000) // 30 minutes
    public void trackAllProductsOnBoot() {
        List<UrlDTO> urls = productRepository.findAll()
                .stream()
                .map(product -> new UrlDTO(product.getUrl()))
                .toList();

        urls.forEach((url) -> {
            rabbitMQService.sendProductUrl("track_product", url);
        });
    }
}
