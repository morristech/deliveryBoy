
package com.yoscholar.deliveryboy.retrofitPojo.ordersToAccept;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Orderdatum {

    @SerializedName("increment_id")
    @Expose
    private String incrementId;
    @SerializedName("order_id")
    @Expose
    private String orderId;
    @SerializedName("customer_name")
    @Expose
    private String customerName;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("pincode")
    @Expose
    private String pincode;
    @SerializedName("method")
    @Expose
    private String method;
    @SerializedName("total")
    @Expose
    private String total;
    @SerializedName("ordertype")
    @Expose
    private String ordertype;
    @SerializedName("ordershipid")
    @Expose
    private String ordershipid;
    @SerializedName("accept_status")
    @Expose
    private String acceptStatus;


    public String getIncrementId() {
        return incrementId;
    }

    public void setIncrementId(String incrementId) {
        this.incrementId = incrementId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getOrdertype() {
        return ordertype;
    }

    public void setOrdertype(String ordertype) {
        this.ordertype = ordertype;
    }

    public String getOrdershipid() {
        return ordershipid;
    }

    public void setOrdershipid(String ordershipid) {
        this.ordershipid = ordershipid;
    }

    public String getAcceptStatus() {
        return acceptStatus;
    }

    public void setAcceptStatus(String acceptStatus) {
        this.acceptStatus = acceptStatus;
    }
}
