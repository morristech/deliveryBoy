package com.yoscholar.deliveryboy.utils;


import com.yoscholar.deliveryboy.retrofitPojo.login.Login;
import com.yoscholar.deliveryboy.retrofitPojo.normalOrders.NormalOrders;
import com.yoscholar.deliveryboy.retrofitPojo.updateOrder.UpdateOrder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


public class RetrofitApi {

    public static String baseUrl = "http://staging1.schoolsaamaan.website";
    public static Retrofit retrofit;

    public static Retrofit getRetrofitInstance() {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(60 * 1000, TimeUnit.MILLISECONDS)
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
        @POST("agrimtool/android/deliveryBoyAPI.php?function=assignedOrders")
        Call<NormalOrders> normalOrders(
                @Field("db_name") String dbName,
                @Field("token") String token
        );

        @FormUrlEncoded
        @POST("agrimtool/android/deliveryBoyAPI.php?function=updateOrder")
        Call<UpdateOrder> updateOrder(
                @Field("order_id") String orderId,
                @Field("status") String status,
                @Field("db_name") String dbName,
                @Field("token") String token
        );
    }
}