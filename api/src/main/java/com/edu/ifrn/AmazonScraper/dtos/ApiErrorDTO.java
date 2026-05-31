package com.edu.ifrn.AmazonScraper.dtos;

import java.time.Instant;
import java.util.List;

public record ApiErrorDTO(
        Instant timestamp,
        int status,
        String error,
        List<String> messages
) {
    public static ApiErrorDTO of(int status, String error, String message) {
        return new ApiErrorDTO(Instant.now(), status, error, List.of(message));
    }
}
