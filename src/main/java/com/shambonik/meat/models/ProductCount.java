package com.shambonik.meat.models;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "product_count_shamb")
@Data
@NoArgsConstructor
public class ProductCount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Order order;

    @ManyToOne
    private Product product;
    private int count;

    public ProductCount(Product product, int count){
        this.product = product;
        this.count = count;
    }
}
