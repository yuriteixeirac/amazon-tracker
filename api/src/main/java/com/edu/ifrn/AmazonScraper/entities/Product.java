package com.edu.ifrn.AmazonScraper.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 512)
    private String title;

    @Column(unique = true, nullable = false, length = 512)
    private String url;

    @JsonIgnore
    @ManyToMany(mappedBy = "trackedProducts")
    Set<User> trackingUsers = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "product")
    Set<ProductRecord> records = new HashSet<>();

    public Product(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id) && Objects.equals(url, product.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, url);
    }
}
