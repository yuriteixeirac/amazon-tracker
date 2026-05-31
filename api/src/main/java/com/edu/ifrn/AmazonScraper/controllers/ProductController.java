package com.edu.ifrn.AmazonScraper.controllers;

import com.edu.ifrn.AmazonScraper.dtos.ProductDTO;
import com.edu.ifrn.AmazonScraper.dtos.ProductRequestDTO;
import com.edu.ifrn.AmazonScraper.dtos.ResponseDTO;
import com.edu.ifrn.AmazonScraper.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> findById(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(ProductDTO.from(productService.findByIdForUser(id, principal.getName())));
    }

    @GetMapping
    public ResponseEntity<List<ProductDTO>> findAll(Principal principal) {
        return ResponseEntity.ok(productService.findByTrackingUser(principal.getName())
                .stream()
                .map(ProductDTO::from)
                .toList());
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> addProduct(@Valid @RequestBody ProductRequestDTO request, Principal principal) {
        productService.trackProduct(principal.getName(), request.url());

        return ResponseEntity.ok(new ResponseDTO("Product registered successfully."));
    }

    @GetMapping("/me")
    public ResponseEntity<List<ProductDTO>> meTrackingProducts(Principal principal) {
        return findAll(principal);
    }
}
