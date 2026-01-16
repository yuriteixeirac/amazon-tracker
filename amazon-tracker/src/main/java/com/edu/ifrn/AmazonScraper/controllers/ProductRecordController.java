package com.edu.ifrn.AmazonScraper.controllers;

import com.edu.ifrn.AmazonScraper.entities.ProductRecord;
import com.edu.ifrn.AmazonScraper.services.ProductRecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/records")
public class ProductRecordController {
    private final ProductRecordService productRecordService;

    public ProductRecordController(ProductRecordService productRecordService) {
        this.productRecordService = productRecordService;
    }

    @GetMapping
    public ResponseEntity<List<ProductRecord>> findAll() {
        return ResponseEntity.ok(productRecordService.findAll());
    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<ProductRecord> findById(@PathVariable Long id) {
//        return ResponseEntity.ok(productRecordService.findById(id));
//    }

    @GetMapping("/{id}")
    public ResponseEntity<List<ProductRecord>> findByProductId(@PathVariable Long id) {
        return ResponseEntity.ok(productRecordService.findByProductId(id));
    }
}
