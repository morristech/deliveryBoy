package com.yoscholar.deliveryboy.couchDB;

import android.content.Context;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.yoscholar.deliveryboy.retrofitPojo.ordersToAccept.Orderdatum;
import com.yoscholar.deliveryboy.utils.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by agrim on 2/11/16.
 */

public class CouchBaseHelper {

    public static final String TAG = CouchBaseHelper.class.getSimpleName();

    private static final String ACCEPTED_ORDERS_DOCUMENT_ID = "acceptedOrders";

    public static final String INCREMENT_ID = "increment_id";
    public static final String ORDER_ID = "order_id";
    public static final String CUSTOMER_NAME = "customer_name";
    public static final String PHONE = "phone";
    public static final String ADDRESS = "address";
    public static final String CITY = "city";
    public static final String PINCODE = "pincode";
    public static final String METHOD = "method";
    public static final String TOTAL = "total";
    public static final String ORDER_TYPE = "ordertype";
    public static final String ORDER_SHIP_ID = "ordershipid";
    public static final String ACCEPT_STATUS = "accept_status";
    private static final String ACTION_DATE = "actionDate";//refers to accept, deliver or failed action


    /**
     * Open database connection
     *
     * @param context context
     * @return database
     */
    public static Database openCouchBaseDB(Context context) {

        Manager manager;
        Database database = null;

        try {
            manager = CouchBaseSingleton.getInstance().getManagerInstance(context);
        } catch (IOException e) {
            Log.e(TAG, "Error : " + e);
        }

        try {
            database = CouchBaseSingleton.getInstance().getDatabaseInstance();
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error : " + e);
        }

        return database;
    }

    /**
     * Saves an accepted order in the DB
     *
     * @param database   database
     * @param orderdatum order data of an order
     * @return boolean indicating whether accepted order was saved or not
     */
    public static boolean saveAcceptedOrderInDB(Database database, Orderdatum orderdatum) {

        boolean flag = true;

        Document acceptedOrdersDocument = database.getDocument(ACCEPTED_ORDERS_DOCUMENT_ID);
        Map<String, Object> map = new HashMap<>();

        if (acceptedOrdersDocument.getProperties() != null)
            map.putAll(acceptedOrdersDocument.getProperties());//put old data from the document in the new map( update )

        Map<String, Object> orderMap = new HashMap<>();
        orderMap.put(INCREMENT_ID, orderdatum.getIncrementId());
        orderMap.put(ORDER_ID, orderdatum.getOrderId());
        orderMap.put(CUSTOMER_NAME, orderdatum.getCustomerName());
        orderMap.put(PHONE, orderdatum.getPhone());
        orderMap.put(ADDRESS, orderdatum.getAddress());
        orderMap.put(CITY, orderdatum.getCity());
        orderMap.put(PINCODE, orderdatum.getPincode());
        orderMap.put(METHOD, orderdatum.getMethod());
        orderMap.put(TOTAL, orderdatum.getTotal());
        orderMap.put(ORDER_TYPE, orderdatum.getOrdertype());
        orderMap.put(ORDER_SHIP_ID, orderdatum.getOrdershipid());
        orderMap.put(ACCEPT_STATUS, 1);
        orderMap.put(ACTION_DATE, Util.getCurrentDateAndTime());

        Document document = database.createDocument();

        try {
            document.putProperties(orderMap);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error putting : " + e);
            flag = false;
        }

        map.put(orderdatum.getIncrementId(), document.getId());// increment_id : document_id


        try {
            acceptedOrdersDocument.putProperties(map);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error putting : " + e);
            flag = false;
        }

        return flag;
    }


    public static ArrayList<Orderdatum> getAllAcceptedOrders(Database database) {

        Document acceptedOrdersDocument = database.getDocument(ACCEPTED_ORDERS_DOCUMENT_ID);
        Map<String, Object> map = acceptedOrdersDocument.getProperties();

        ArrayList<Orderdatum> orderdatumArrayList = new ArrayList<>();

        if (map != null) {
            Iterator it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                if (pair.getKey().equals("_id") || pair.getKey().equals("_rev"))
                    ;
                else {
                    //System.out.println(pair.getKey() + " = " + pair.getValue());
                    Document document = database.getDocument(pair.getValue().toString());

                    Orderdatum orderdatum = new Orderdatum();
                    orderdatum.setIncrementId(document.getProperty(INCREMENT_ID).toString());
                    orderdatum.setOrderId(document.getProperty(ORDER_ID).toString());
                    orderdatum.setCustomerName(document.getProperty(CUSTOMER_NAME).toString());
                    orderdatum.setPhone(document.getProperty(PHONE).toString());
                    orderdatum.setAddress(document.getProperty(ADDRESS).toString());
                    orderdatum.setCity(document.getProperty(CITY).toString());
                    orderdatum.setPincode(document.getProperty(PINCODE).toString());
                    orderdatum.setMethod(document.getProperty(METHOD).toString());
                    orderdatum.setTotal(document.getProperty(TOTAL).toString());
                    orderdatum.setOrdertype(document.getProperty(ORDER_TYPE).toString());
                    orderdatum.setOrdershipid(document.getProperty(ORDER_SHIP_ID).toString());
                    orderdatum.setAcceptStatus(Integer.parseInt(document.getProperty(ACCEPT_STATUS).toString()));

                    orderdatumArrayList.add(orderdatum);
                }
            }
        }

        return orderdatumArrayList;
    }
}
