package com.shambonik.meat.repositories;

import com.shambonik.meat.models.ProductCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductCountRepo extends JpaRepository<ProductCount, Long> {
}
