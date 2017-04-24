package com.yoscholar.deliveryboy.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.couchbase.lite.Database;
import com.google.gson.Gson;
import com.yoscholar.deliveryboy.couchDB.CouchBaseHelper;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Map;

public class DeliveredOrdersService extends IntentService {

    public static final String TAG = DeliveredOrdersService.class.getSimpleName();

    public DeliveredOrdersService() {
        super("DeliveredOrdersService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Database database = CouchBaseHelper.openCouchBaseDB(this);

        ArrayList<Map<String, Object>> orderMapArrayList = CouchBaseHelper.getAllDeliveredOrders(database);

        Gson gson = new Gson();

        Log.d(TAG, gson.toJson(orderMapArrayList));
        //Log.d(TAG, new JSONArray(orderMapArrayList).toString());

    }

}
