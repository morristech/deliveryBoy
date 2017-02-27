package com.yoscholar.deliveryboy.pojo;

/**
 * Created by agrim on 27/2/17.
 */

public class OrderAccepted {

    private boolean accepted;

    public OrderAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
}
