package com.edu.ifrn.AmazonScraper.services;

import com.edu.ifrn.AmazonScraper.dtos.UrlDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQService {
    private final RabbitTemplate rabbitTemplate;

    public RabbitMQService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendProductUrl(String queue, UrlDTO urlDTO) {
        rabbitTemplate.convertAndSend("", queue, urlDTO);
    }
}
