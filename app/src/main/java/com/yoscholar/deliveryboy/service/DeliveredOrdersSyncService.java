package com.yoscholar.deliveryboy.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.couchbase.lite.Database;
import com.google.gson.Gson;
import com.yoscholar.deliveryboy.couchDB.CouchBaseHelper;
import com.yoscholar.deliveryboy.pojo.DeliveredOrdersUpdated;
import com.yoscholar.deliveryboy.retrofitPojo.syncResponse.SyncResponse;
import com.yoscholar.deliveryboy.utils.AppPreference;
import com.yoscholar.deliveryboy.utils.RetrofitApi;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

public class DeliveredOrdersSyncService extends IntentService {

    public static final String TAG = DeliveredOrdersSyncService.class.getSimpleName();


    public DeliveredOrdersSyncService() {
        super("DeliveredOrdersSyncService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d(TAG, "Is delivered orders sync service running : " + AppPreference.getBoolean(this, AppPreference.IS_DELIVERED_ORDERS_SYNC_SERVICE_RUNNING));
        Timber.d("Is delivered orders sync service running : " + AppPreference.getBoolean(this, AppPreference.IS_DELIVERED_ORDERS_SYNC_SERVICE_RUNNING));

        Log.d(TAG, "DeliveredOrdersSyncService started.........");
        Timber.d("DeliveredOrdersSyncService started.........");
        AppPreference.saveBoolean(this, AppPreference.IS_DELIVERED_ORDERS_SYNC_SERVICE_RUNNING, true);

        Database database = CouchBaseHelper.openCouchBaseDB(this);

        ArrayList<Map<String, Object>> orderMapArrayList = null;

        try {

            orderMapArrayList = CouchBaseHelper.getAllUnSyncedDeliveredOrders(database);

        } catch (Exception e) {

            Log.d(TAG, "Exception caught while getting unsynced orders : " + e);
            Timber.d("Exception caught while getting unsynced orders : " + e);

        }

        Gson gson = new Gson();

        if (orderMapArrayList != null && orderMapArrayList.size() != 0) {

            try {

                Log.d(TAG, gson.toJson(orderMapArrayList));
                syncDeliveredOrders(gson.toJson(orderMapArrayList));

            } catch (Exception e) {

                Log.d(TAG, "Exception caught while converting to json: " + e);
                Timber.d("Exception caught while converting to json: " + e);

            }

        } else {

            Log.d(TAG, "No delivered orders to sync.");
            Timber.d("No delivered orders to sync.");
            Log.d(TAG, "DeliveredOrdersSyncService stopped.........");
            Timber.d("DeliveredOrdersSyncService stopped.........");
            AppPreference.saveBoolean(this, AppPreference.IS_DELIVERED_ORDERS_SYNC_SERVICE_RUNNING, false);

        }
    }

    private void syncDeliveredOrders(String deliveredOrdersJson) {

        RetrofitApi.ApiInterface apiInterface = RetrofitApi.getApiInterfaceInstance();

        Call<SyncResponse> syncCall = apiInterface.syncDeliveredOrders(
                AppPreference.getString(this, AppPreference.NAME),//db name
                AppPreference.getString(this, AppPreference.TOKEN),//jwt token
                deliveredOrdersJson
        );

        try {

            Response response = syncCall.execute();

            if (response.isSuccessful()) {

                SyncResponse syncResponse = (SyncResponse) response.body();

                if (syncResponse.getStatus().equalsIgnoreCase("success")) {

                    boolean flag = updateDeliveredOrders(syncResponse);

                    if (flag) {

                        Log.d(TAG, "Orders synced and updated.");
                        Timber.d("Orders synced and updated.");

                        EventBus.getDefault().post(new DeliveredOrdersUpdated(true));

                    } else {

                        Log.d(TAG, "Orders synced and but couldn't be updated.");
                        Timber.d("Orders synced and but couldn't be updated.");

                    }

                } else if (syncResponse.getStatus().equalsIgnoreCase("failure")) {

                    Log.d(TAG, syncResponse.getMessage());
                    Timber.d(syncResponse.getMessage());

                }

                Log.d(TAG, "DeliveredOrdersSyncService stopped.........");
                Timber.d("DeliveredOrdersSyncService stopped.........");
                AppPreference.saveBoolean(this, AppPreference.IS_DELIVERED_ORDERS_SYNC_SERVICE_RUNNING, false);

            }

        } catch (IOException e) {

            Log.e(TAG, "Error while syncing delivered orders : " + e);
            Timber.e("Error while syncing delivered orders : " + e);

        } finally {

            Log.d(TAG, "DeliveredOrdersSyncService stopped.........");
            Timber.d("DeliveredOrdersSyncService stopped.........");
            AppPreference.saveBoolean(this, AppPreference.IS_DELIVERED_ORDERS_SYNC_SERVICE_RUNNING, false);

        }

    }

    private boolean updateDeliveredOrders(SyncResponse syncResponse) {

        Database database = CouchBaseHelper.openCouchBaseDB(this);
        boolean flag = CouchBaseHelper.updateDeliveredOrdersSyncStatus(database, syncResponse);

        return flag;
    }

}
