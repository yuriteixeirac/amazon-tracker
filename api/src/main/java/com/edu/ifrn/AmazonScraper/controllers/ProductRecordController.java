package com.edu.ifrn.AmazonScraper.controllers;

import com.edu.ifrn.AmazonScraper.dtos.ProductRecordDTO;
import com.edu.ifrn.AmazonScraper.services.ProductRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/records")
public class ProductRecordController {
    private final ProductRecordService productRecordService;

    public ProductRecordController(ProductRecordService productRecordService) {
        this.productRecordService = productRecordService;
    }

    @GetMapping
    public ResponseEntity<List<ProductRecordDTO>> findAll(Principal principal) {
        return ResponseEntity.ok(productRecordService.findByUser(principal.getName())
                .stream()
                .map(ProductRecordDTO::from)
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<ProductRecordDTO>> findByProductId(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(productRecordService.findByProductIdForUser(id, principal.getName())
                .stream()
                .map(ProductRecordDTO::from)
                .toList());
    }
}
