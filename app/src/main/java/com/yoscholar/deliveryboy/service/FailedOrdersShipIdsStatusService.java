package com.yoscholar.deliveryboy.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.couchbase.lite.Database;
import com.yoscholar.deliveryboy.couchDB.CouchBaseHelper;
import com.yoscholar.deliveryboy.pojo.FailedOrdersUpdated;
import com.yoscholar.deliveryboy.retrofitPojo.getShipIdsStatus.Stat;
import com.yoscholar.deliveryboy.retrofitPojo.getShipIdsStatus.Status;
import com.yoscholar.deliveryboy.utils.AppPreference;
import com.yoscholar.deliveryboy.utils.RetrofitApi;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;

import java.io.IOException;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;


public class FailedOrdersShipIdsStatusService extends IntentService {

    private static final String TAG = FailedOrdersShipIdsStatusService.class.getSimpleName();


    public FailedOrdersShipIdsStatusService() {
        super("FailedOrdersShipIdsStatusService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d(TAG, "FailedOrdersShipIdsStatusService started.........");
        AppPreference.saveBoolean(this, AppPreference.IS_FAILED_ORDERS_SHIP_IDS_STATUS_SERVICE_RUNNING, true);

        JSONArray orderShipIdsJsonArray = new JSONArray();
        Database database = CouchBaseHelper.openCouchBaseDB(this);

        for (Map<String, Object> orderMap : CouchBaseHelper.getAllFailedOrders(database))
            orderShipIdsJsonArray.put(orderMap.get(CouchBaseHelper.ORDER_SHIP_ID).toString());

        if (orderShipIdsJsonArray.length() != 0) {

            getShipIdsStatus(orderShipIdsJsonArray);

        } else {

            Log.d(TAG, "No failed orders to check ship ids status.");
            Log.d(TAG, "FailedOrdersShipIdsStatusService stopped.........");
            AppPreference.saveBoolean(this, AppPreference.IS_FAILED_ORDERS_SHIP_IDS_STATUS_SERVICE_RUNNING, false);

        }


        Log.d(TAG, "Is failed orders sync service running : " + AppPreference.getBoolean(this, AppPreference.IS_FAILED_ORDERS_SYNC_SERVICE_RUNNING));
        if (!AppPreference.getBoolean(this, AppPreference.IS_FAILED_ORDERS_SYNC_SERVICE_RUNNING))
            startService(new Intent(this, FailedOrdersSyncService.class));
    }

    public void getShipIdsStatus(JSONArray orderShipIdsJsonArray) {

        RetrofitApi.ApiInterface apiInterface = RetrofitApi.getApiInterfaceInstance();
        Call<Status> statusCall = apiInterface.getShipIdsStatus(
                AppPreference.getString(this, AppPreference.NAME),//db name
                orderShipIdsJsonArray.toString(),
                AppPreference.getString(this, AppPreference.TOKEN)//jwt token
        );

        try {

            Response response = statusCall.execute();

            if (response.isSuccessful()) {

                Status status = (Status) response.body();

                if (status.getStatus().equalsIgnoreCase("success")) {

                    Database database = CouchBaseHelper.openCouchBaseDB(this);

                    for (Stat stat : status.getStats()) {

                        if (stat.getRstatus().equals("1"))
                            CouchBaseHelper.deleteAFailedOrderFromDB(database, stat.getOrderShipId());

                    }

                    EventBus.getDefault().post(new FailedOrdersUpdated(true));


                } else if (status.getStatus().equalsIgnoreCase("failure")) {

                    Log.d(TAG, status.getMessage());
                }

                Log.d(TAG, "FailedOrdersShipIdsStatusService stopped.........");
                AppPreference.saveBoolean(this, AppPreference.IS_FAILED_ORDERS_SHIP_IDS_STATUS_SERVICE_RUNNING, false);

            }

        } catch (IOException e) {

            Log.e(TAG, "Error while getting failed orders ship ids status : " + e);

        } finally {

            Log.d(TAG, "FailedOrdersShipIdsStatusService stopped.........");
            AppPreference.saveBoolean(this, AppPreference.IS_FAILED_ORDERS_SHIP_IDS_STATUS_SERVICE_RUNNING, false);
        }
    }
}
