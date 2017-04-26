package com.yoscholar.deliveryboy.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.couchbase.lite.Database;
import com.google.gson.Gson;
import com.yoscholar.deliveryboy.couchDB.CouchBaseHelper;
import com.yoscholar.deliveryboy.pojo.FailedOrdersUpdated;
import com.yoscholar.deliveryboy.retrofitPojo.syncResponse.SyncResponse;
import com.yoscholar.deliveryboy.utils.AppPreference;
import com.yoscholar.deliveryboy.utils.RetrofitApi;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;


public class FailedOrdersSyncService extends IntentService {

    private static final String TAG = FailedOrdersSyncService.class.getSimpleName();

    public FailedOrdersSyncService() {
        super("FailedOrdersSyncService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d(TAG, "FailedOrdersSyncService started.........");
        AppPreference.saveBoolean(this, AppPreference.IS_FAILED_ORDERS_SYNC_SERVICE_RUNNING, true);

        Database database = CouchBaseHelper.openCouchBaseDB(this);

        ArrayList<Map<String, Object>> orderMapArrayList = CouchBaseHelper.getAllUnSyncedFailedOrders(database);

        Gson gson = new Gson();

        if (orderMapArrayList.size() != 0) {

            Log.d(TAG, gson.toJson(orderMapArrayList));
            syncFailedOrders(gson.toJson(orderMapArrayList));

        } else {

            Log.d(TAG, "No failed orders to sync.");
            Log.d(TAG, "FailedOrdersSyncService stopped.........");
            AppPreference.saveBoolean(this, AppPreference.IS_FAILED_ORDERS_SYNC_SERVICE_RUNNING, false);

        }

    }


    private void syncFailedOrders(String failedOrdersJson) {

        RetrofitApi.ApiInterface apiInterface = RetrofitApi.getApiInterfaceInstance();

        Call<SyncResponse> syncCall = apiInterface.syncFailedOrders(
                AppPreference.getString(this, AppPreference.NAME),//db name
                AppPreference.getString(this, AppPreference.TOKEN),//jwt token
                failedOrdersJson
        );

        try {

            Response response = syncCall.execute();

            if (response.isSuccessful()) {

                SyncResponse syncResponse = (SyncResponse) response.body();

                if (syncResponse.getStatus().equalsIgnoreCase("success")) {

                    boolean flag = updateFailedOrders(syncResponse);

                    if (flag) {

                        Log.d(TAG, "Orders synced and updated.");

                        EventBus.getDefault().post(new FailedOrdersUpdated(true));

                    } else
                        Log.d(TAG, "Orders synced and but couldn't be updated.");

                } else if (syncResponse.getStatus().equalsIgnoreCase("failure")) {

                    Log.d(TAG, syncResponse.getMessage());

                }

                Log.d(TAG, "FailedOrdersSyncService stopped.........");
                AppPreference.saveBoolean(this, AppPreference.IS_FAILED_ORDERS_SYNC_SERVICE_RUNNING, false);

            }

        } catch (IOException e) {

            Log.e(TAG, "Error while syncing failed orders : " + e);

        } finally {

            Log.d(TAG, "FailedOrdersSyncService stopped.........");
            AppPreference.saveBoolean(this, AppPreference.IS_FAILED_ORDERS_SYNC_SERVICE_RUNNING, false);

        }

    }

    private boolean updateFailedOrders(SyncResponse syncResponse) {

        Database database = CouchBaseHelper.openCouchBaseDB(this);
        boolean flag = CouchBaseHelper.updateFailedOrdersSyncStatus(database, syncResponse);

        return flag;
    }

}
