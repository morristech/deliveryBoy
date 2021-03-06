package com.yoscholar.deliveryboy.utils;


import com.yoscholar.deliveryboy.retrofitPojo.acceptAnOrder.AcceptOrder;
import com.yoscholar.deliveryboy.retrofitPojo.getShipIdsStatus.Status;
import com.yoscholar.deliveryboy.retrofitPojo.login.Login;
import com.yoscholar.deliveryboy.retrofitPojo.ordersToAccept.AcceptOrders;
import com.yoscholar.deliveryboy.retrofitPojo.syncResponse.SyncResponse;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;


public class RetrofitApi {

    public static String baseUrl = "https://www.yoscholar.com";
    public static Retrofit retrofit;

    public static Retrofit getRetrofitInstance() {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(5 * 60 * 1000, TimeUnit.MILLISECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }

    public static ApiInterface getApiInterfaceInstance() {
        return getRetrofitInstance().create(ApiInterface.class);
    }

    public interface ApiInterface {

        @FormUrlEncoded
        @POST("agrimtool/android/deliveryBoyAPI.php?function=login")
        Call<Login> login(
                @Field("username") String username,
                @Field("password") String password
        );

        @FormUrlEncoded
        @POST("agrimtool/android/deliveryBoyAPI.php?function=assignedOrdersToAccept")
        Call<AcceptOrders> ordersToAccept(
                @Field("db_name") String dbName,
                @Field("token") String token,
                @Field("date") String date
        );

        @FormUrlEncoded
        @POST("agrimtool/android/deliveryBoyAPI.php?function=acceptOrDecline")
        Call<AcceptOrder> acceptOrDeclineAnOrder(
                @Field("order_ship_id") String orderShipId,
                @Field("token") String token,
                @Field("flag") String flag,// "1" = accept/"0" = decline
                @Field("db_name") String dbName,
                @Field("increment_id") String incrementId
        );

/*
        @FormUrlEncoded
        @POST("agrimtool/android/deliveryBoyAPI.php?function=updateOrder")
        Call<UpdateOrder> updateOrder(
                @Field("increment_id") String incrementId,
                @Field("comment") String comment,
                @Field("order_ship_id") String orderShipId,
                @Field("status") String status,
                @Field("db_name") String dbName,
                @Field("token") String token
        );
*/

        @GET
        Call<ResponseBody> sendMessage(
                @Url String url,
                @Query("workingkey") String workingKey,
                @Query("to") String to,
                @Query("sender") String sender,
                @Query("message") String message

        );

       /* @FormUrlEncoded
        @POST("agrimtool/android/deliveryBoyAPI.php?function=redeliver")
        Call<ReDeliver> redeliver(
                @Field("increment_id") String incrementId,
                @Field("order_ship_id") String orderShipId,
                @Field("db_name") String dbName,
                @Field("token") String token
        );*/

        @FormUrlEncoded
        @POST("agrimtool/android/deliveryBoyAPI.php?function=getShipIdsStatus")
        Call<Status> getShipIdsStatus(
                @Field("db_name") String dbName,
                @Field("order_ship_ids") String orderShipIdsJsonArray,
                @Field("token") String token
        );

        @FormUrlEncoded
        @POST("agrimtool/android/deliveryBoyAPI.php?function=syncDeliveredOrders")
        Call<SyncResponse> syncDeliveredOrders(
                @Field("db_name") String dbName,
                @Field("token") String token,
                @Field("delivered_orders_json") String deliveredOrdersJson
        );

        @FormUrlEncoded
        @POST("agrimtool/android/deliveryBoyAPI.php?function=syncFailedOrders")
        Call<SyncResponse> syncFailedOrders(
                @Field("db_name") String dbName,
                @Field("token") String token,
                @Field("failed_orders_json") String failedOrdersJson
        );
    }
}