package com.edu.ifrn.AmazonScraper.services;

import com.edu.ifrn.AmazonScraper.dtos.ProductTrackMessageDTO;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQService {
    private final RabbitTemplate rabbitTemplate;
    private final String trackProductQueue;

    public RabbitMQService(
            RabbitTemplate rabbitTemplate,
            @Value("${app.rabbitmq.track-product-queue}") String trackProductQueue
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.trackProductQueue = trackProductQueue;
    }

    public void sendProductTrackMessage(ProductTrackMessageDTO message) {
        rabbitTemplate.convertAndSend("", trackProductQueue, message, rabbitMessage -> {
            rabbitMessage.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            return rabbitMessage;
        });
    }
}
