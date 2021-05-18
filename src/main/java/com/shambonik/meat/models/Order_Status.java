package com.shambonik.meat.models;

public enum Order_Status {
    PROCESSING, COLLECTING, DELIVERING, COMPLETED, CANCELED;

    public static Order_Status[] getStatuses()
    {
        return Order_Status.values();
    }
}
