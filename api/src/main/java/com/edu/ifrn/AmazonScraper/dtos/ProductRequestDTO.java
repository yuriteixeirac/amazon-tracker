package com.edu.ifrn.AmazonScraper.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record ProductRequestDTO(
        @NotBlank @URL @Size(max = 512) String url
) {}
