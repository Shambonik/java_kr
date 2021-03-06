package com.shambonik.meat.repositories;


import com.shambonik.meat.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {
    Order findOrderById(long id);
}
