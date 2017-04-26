package com.yoscholar.deliveryboy.pojo;

/**
 * Created by agrim on 26/4/17.
 */

public class FailedOrdersUpdated {

    private boolean ordersUpdated;

    public FailedOrdersUpdated(boolean ordersUpdated) {
        this.ordersUpdated = ordersUpdated;
    }

    public boolean isOrdersUpdated() {
        return ordersUpdated;
    }

    public void setOrdersUpdated(boolean ordersUpdated) {
        this.ordersUpdated = ordersUpdated;
    }
}
