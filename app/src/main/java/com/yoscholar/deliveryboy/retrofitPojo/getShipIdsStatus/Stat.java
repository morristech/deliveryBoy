
package com.yoscholar.deliveryboy.retrofitPojo.getShipIdsStatus;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Stat {

    @SerializedName("order_ship_id")
    @Expose
    private String orderShipId;
    @SerializedName("rstatus")
    @Expose
    private String rstatus;

    public String getOrderShipId() {
        return orderShipId;
    }

    public void setOrderShipId(String orderShipId) {
        this.orderShipId = orderShipId;
    }

    public String getRstatus() {
        return rstatus;
    }

    public void setRstatus(String rstatus) {
        this.rstatus = rstatus;
    }

}
