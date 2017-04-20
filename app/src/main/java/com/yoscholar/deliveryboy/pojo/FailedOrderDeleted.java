package com.yoscholar.deliveryboy.pojo;

/**
 * Created by agrim on 20/4/17.
 */

public class FailedOrderDeleted {

    private boolean deleted;

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public FailedOrderDeleted(boolean deleted) {

        this.deleted = deleted;
    }
}
