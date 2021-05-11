package com.shambonik.meat.models;

import lombok.Data;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "products_shamb")
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String description;

    @ElementCollection(targetClass = Product_Category.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "product_category_shamb", joinColumns = @JoinColumn(name = "product_id"))
    @Enumerated(EnumType.STRING)
    private Set<Product_Category> category;

    private String imageName;
    private int count = 0;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<ProductCount> countInOrders;

    public void setAll(String name, String description, Set<Product_Category> category, int count){
        this.name = name;
        this.description = description;
        this.category = category;
        this.count = count;
    }
}
