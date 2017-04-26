
package com.yoscholar.deliveryboy.retrofitPojo.syncResponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SyncResponse {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("order_ship_ids")
    @Expose
    private List<String> orderShipIds = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getOrderShipIds() {
        return orderShipIds;
    }

    public void setOrderShipIds(List<String> orderShipIds) {
        this.orderShipIds = orderShipIds;
    }

}
