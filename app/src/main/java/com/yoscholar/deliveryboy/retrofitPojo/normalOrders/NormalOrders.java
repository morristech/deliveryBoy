
package com.yoscholar.deliveryboy.retrofitPojo.normalOrders;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NormalOrders {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("orderdata")
    @Expose
    private List<Orderdatum> orderdata = null;

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

    public List<Orderdatum> getOrderdata() {
        return orderdata;
    }

    public void setOrderdata(List<Orderdatum> orderdata) {
        this.orderdata = orderdata;
    }

}
