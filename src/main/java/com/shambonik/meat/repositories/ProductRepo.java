package com.shambonik.meat.repositories;

import com.shambonik.meat.models.Product;
import com.shambonik.meat.models.Product_Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {
    List<Product> findAll();
    Product findProductById(Long id);
    List<Product> findProductsByCategory(Product_Category category);
}
