package com.shambonik.meat.models;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "orders_shamb")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name = "";
    private String address = "";
    private String email = "";
    private String phone = "";
    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER)
    private List<ProductCount> products;
}
