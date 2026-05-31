package com.edu.ifrn.AmazonScraper.services;

import com.edu.ifrn.AmazonScraper.dtos.ProductTrackMessageDTO;
import com.edu.ifrn.AmazonScraper.entities.Product;
import com.edu.ifrn.AmazonScraper.repositories.ProductRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "app.product-tracking.scheduler-enabled", havingValue = "true", matchIfMissing = true)
public class ScheduleService {
    private static final String SCHEDULER_LOCK_NAME = "price-pulse-track-all-products";
    private static final int PAGE_SIZE = 100;

    private final ProductRepository productRepository;
    private final RabbitMQService rabbitMQService;
    private final JdbcTemplate jdbcTemplate;

    public ScheduleService(
            ProductRepository productRepository,
            RabbitMQService rabbitMQService,
            JdbcTemplate jdbcTemplate
    ) {
        this.productRepository = productRepository;
        this.rabbitMQService = rabbitMQService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Scheduled(
            fixedDelayString = "${app.product-tracking.fixed-delay-ms}",
            initialDelayString = "${app.product-tracking.initial-delay-ms}"
    )
    public void trackAllProducts() {
        if (!tryAcquireSchedulerLock()) {
            return;
        }

        try {
            int pageNumber = 0;
            Page<Product> page;

            do {
                page = productRepository.findAll(PageRequest.of(pageNumber, PAGE_SIZE));
                page.forEach(product -> rabbitMQService.sendProductTrackMessage(
                        new ProductTrackMessageDTO(product.getId(), product.getUrl())
                ));
                pageNumber++;
            } while (page.hasNext());
        } finally {
            jdbcTemplate.queryForObject("SELECT RELEASE_LOCK(?)", Integer.class, SCHEDULER_LOCK_NAME);
        }
    }

    private boolean tryAcquireSchedulerLock() {
        Integer result = jdbcTemplate.queryForObject("SELECT GET_LOCK(?, 0)", Integer.class, SCHEDULER_LOCK_NAME);
        return result != null && result == 1;
    }
}
