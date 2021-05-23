package com.shambonik.meat.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "orders_shamb")
@Data
public class Order{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    private User user;
    private String name = "";
    private String address = "";
    private String email = "";
    private String phone = "";
    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER)
    private List<ProductCount> products;

    @ElementCollection(targetClass = Order_Status.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "order_status_shamb", joinColumns = @JoinColumn(name = "order_id"))
    @Enumerated(EnumType.STRING)
    private Set<Order_Status> status;

    public void setData(String name, String address, String email, String phone){
        this.name = name;
        this.address = address;
        this.email = email;
        this.phone = phone;
    }
}
