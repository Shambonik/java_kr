package com.shambonik.meat;

import com.shambonik.meat.models.Product;
import lombok.Data;

@Data
public class Pair{
    private Product product;
    private long count;

    public Pair(Product product, long count) {
        this.product = product;
        this.count = count;
    }
}
