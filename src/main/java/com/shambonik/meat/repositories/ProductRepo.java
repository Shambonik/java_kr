package com.shambonik.meat.repositories;

import com.shambonik.meat.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepo extends JpaRepository<Product, Long> {
    List<Product> findAll();
    Product findProductById(Long id);
}
