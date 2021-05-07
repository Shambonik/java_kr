package com.shambonik.meat.models;

public enum Product_Category {
    PORK, CHICKEN, BEEF, MUTTON;

    public static Product_Category[] getCategories()
    {
        return Product_Category.values();
    }
}
