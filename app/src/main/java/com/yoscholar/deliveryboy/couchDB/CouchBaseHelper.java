package com.yoscholar.deliveryboy.couchDB;

import android.content.Context;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Manager;
import com.yoscholar.deliveryboy.retrofitPojo.ordersToAccept.Orderdatum;
import com.yoscholar.deliveryboy.retrofitPojo.syncResponse.SyncResponse;
import com.yoscholar.deliveryboy.utils.Util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Created by agrim on 2/11/16.
 */

public class CouchBaseHelper {

    private static final String TAG = CouchBaseHelper.class.getSimpleName();

    private static final String ACCEPTED_ORDERS_DOCUMENT_ID = "acceptedOrders";
    private static final String DELIVERED_ORDERS_DOCUMENT_ID = "deliveredOrders";
    private static final String FAILED_ORDERS_DOCUMENT_ID = "failedOrders";

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
    public static final String ACTION_DATE = "action_date";//refers to accept, deliver or failed action date
    public static final String PAY_MODE = "pay_mode";//refers to pay mode in case of COD or PREPAID

    //PAYMENT METHOD------> COD------------PREPAID
    //                       |               |
    //                       |----Cash       |-----Prepaid
    //                       |----Card
    //                       |----Cheque
    //                       |----Wallet

    public static final String ACCEPTED_DATE = "accepted_date";
    public static final String SYNC_STATUS = "sync_status";
    private static final String COMMENT = "comment";
    private static final String COMMENT_ID = "comment_id";

    //PAYMENT METHODS
    public static final String PAYMENT_COD = "COD";
    public static final String PAYMENT_PREPAID = "Prepaid";

    //PAY_MODE TYPES
    public static final String PAY_MODE_CASH = "Cash";
    public static final String PAY_MODE_CARD = "Card";
    public static final String PAY_MODE_CHEQUE = "Cheque";
    public static final String PAY_MODE_WALLET = "Wallet";


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
    public static boolean saveAnAcceptedOrderInDB(Database database, Orderdatum orderdatum) {

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
        orderMap.put(ACCEPT_STATUS, "1");
        //ADD ACCEPTED_DATE
        orderMap.put(ACCEPTED_DATE, Util.getCurrentDate());

        Document document = database.createDocument();

        try {
            document.putProperties(orderMap);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error putting : " + e);
            flag = false;
        }

        map.put(orderdatum.getOrdershipid(), document.getId());// ordershipid : document_id

        try {
            acceptedOrdersDocument.putProperties(map);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error putting : " + e);
            flag = false;
        }

        return flag;
    }


    /**
     * Get all accepted orders from the DB
     *
     * @param database database
     * @return arrayList containing all accepted orders
     */
    public static ArrayList<Map<String, Object>> getAllAcceptedOrders(Database database) {

        Document acceptedOrdersDocument = database.getDocument(ACCEPTED_ORDERS_DOCUMENT_ID);
        Map<String, Object> map = acceptedOrdersDocument.getProperties();

        ArrayList<Map<String, Object>> ordersArrayList = new ArrayList<>();

        if (map != null) {
            Iterator it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                if (pair.getKey().equals("_id") || pair.getKey().equals("_rev"))
                    ;
                else {
                    //System.out.println(pair.getKey() + " = " + pair.getValue());
                    Document document = database.getDocument(pair.getValue().toString());

                    Map<String, Object> orderMap = new HashMap<>();
                    orderMap.put(INCREMENT_ID, document.getProperty(INCREMENT_ID).toString());
                    orderMap.put(ORDER_ID, document.getProperty(ORDER_ID).toString());
                    orderMap.put(CUSTOMER_NAME, document.getProperty(CUSTOMER_NAME).toString());
                    orderMap.put(PHONE, document.getProperty(PHONE).toString());
                    orderMap.put(ADDRESS, document.getProperty(ADDRESS).toString());
                    orderMap.put(CITY, document.getProperty(CITY).toString());
                    orderMap.put(PINCODE, document.getProperty(PINCODE).toString());
                    orderMap.put(METHOD, document.getProperty(METHOD).toString());
                    orderMap.put(TOTAL, document.getProperty(TOTAL).toString());
                    orderMap.put(ORDER_TYPE, document.getProperty(ORDER_TYPE).toString());
                    orderMap.put(ORDER_SHIP_ID, document.getProperty(ORDER_SHIP_ID).toString());
                    orderMap.put(ACCEPT_STATUS, document.getProperty(ACCEPT_STATUS).toString());
                    orderMap.put(ACCEPTED_DATE, document.getProperty(ACCEPTED_DATE).toString());

                    ordersArrayList.add(orderMap);
                }
            }
        }

        return ordersArrayList;
    }

    /**
     * Get an accepted order from the DB
     *
     * @param orderShipId ordersShipId
     * @param database    database
     * @return accepted order Map
     */
    public static Map<String, Object> getAnAcceptedOrder(Database database, String orderShipId) {

        Document acceptedOrdersDocument = database.getDocument(ACCEPTED_ORDERS_DOCUMENT_ID);
        Map<String, Object> map = acceptedOrdersDocument.getProperties();

        Map<String, Object> orderMap = null;

        if (map != null) {
            Iterator it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                if (pair.getKey().equals("_id") || pair.getKey().equals("_rev"))
                    ;
                else {
                    //System.out.println(pair.getKey() + " = " + pair.getValue());

                    if (orderShipId.equals(pair.getKey().toString())) {

                        //delete the document
                        Document document = database.getDocument(pair.getValue().toString());

                        orderMap = new HashMap<>();
                        orderMap.put(INCREMENT_ID, document.getProperty(INCREMENT_ID).toString());
                        orderMap.put(ORDER_ID, document.getProperty(ORDER_ID).toString());
                        orderMap.put(CUSTOMER_NAME, document.getProperty(CUSTOMER_NAME).toString());
                        orderMap.put(PHONE, document.getProperty(PHONE).toString());
                        orderMap.put(ADDRESS, document.getProperty(ADDRESS).toString());
                        orderMap.put(CITY, document.getProperty(CITY).toString());
                        orderMap.put(PINCODE, document.getProperty(PINCODE).toString());
                        orderMap.put(METHOD, document.getProperty(METHOD).toString());
                        orderMap.put(TOTAL, document.getProperty(TOTAL).toString());
                        orderMap.put(ORDER_TYPE, document.getProperty(ORDER_TYPE).toString());
                        orderMap.put(ORDER_SHIP_ID, document.getProperty(ORDER_SHIP_ID).toString());
                        orderMap.put(ACCEPT_STATUS, document.getProperty(ACCEPT_STATUS).toString());
                        orderMap.put(ACCEPTED_DATE, document.getProperty(ACCEPTED_DATE).toString());

                    }
                }
            }
        }

        return orderMap;
    }

    /**
     * delete an accepted order from the DB
     *
     * @param database    database
     * @param orderShipId orderShipId of the order
     * @return boolean indicating whether the operation was successful or not
     */
    public static boolean deleteAnAcceptedOrderFromDB(Database database, String orderShipId) {

        boolean flag = true;

        Document acceptedOrdersDocument = database.getDocument(ACCEPTED_ORDERS_DOCUMENT_ID);
        Map<String, Object> map = new HashMap<>();

        String keyToDelete = null;

        if (acceptedOrdersDocument.getProperties() != null)
            map.putAll(acceptedOrdersDocument.getProperties());//put old data from the document in the new map( update )

        if (map != null) {
            Iterator it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                if (pair.getKey().equals("_id") || pair.getKey().equals("_rev"))
                    ;
                else {
                    //System.out.println(pair.getKey() + " = " + pair.getValue());
                    if (orderShipId.equals(pair.getKey().toString())) {

                        keyToDelete = orderShipId;

                        //delete the document
                        Document document = database.getDocument(pair.getValue().toString());

                        try {

                            document.purge();

                        } catch (CouchbaseLiteException e) {

                            Log.d(TAG, "Error while purging : " + e);
                            flag = false;

                        }

                    }

                }
            }
        }


        if (keyToDelete != null) {

            //remove entry from acceptedOrdersDocument
            map.remove(keyToDelete);
            try {

                acceptedOrdersDocument.putProperties(map);

            } catch (CouchbaseLiteException e) {

                Log.e(TAG, "Error putting : " + e);
                flag = false;

            }
        }

        Log.d(TAG, "accepted orders  : " + acceptedOrdersDocument.getProperties());

        return flag;

    }


    /**
     * save a delivered order in the DB
     *
     * @param database database
     * @param orderMap orderMap
     * @param payMode  payMode
     * @return boolean indicating whether the action was successful or not
     */
    public static boolean saveDeliveredOrderInDB(Database database, Map<String, Object> orderMap, String payMode, String comment) {

        Document deliveredOrdersDocument = database.getDocument(DELIVERED_ORDERS_DOCUMENT_ID);
        Map<String, Object> map = new HashMap<>();

        boolean flag = true;

        if (deliveredOrdersDocument.getProperties() != null)
            map.putAll(deliveredOrdersDocument.getProperties());//put old data from the document in the new map( update )

        orderMap.put(ACTION_DATE, Util.getCurrentDate());
        orderMap.put(PAY_MODE, payMode);
        orderMap.put(SYNC_STATUS, "0");
        orderMap.put(COMMENT, comment);
        //orderMap.put(COMMENT_ID, Util.generateCommentId());

        Document document = database.createDocument();

        try {
            document.putProperties(orderMap);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error putting : " + e);
            flag = false;
        }

        map.put(orderMap.get(ORDER_SHIP_ID).toString(), document.getId());// ordershipid : document_id

        try {
            deliveredOrdersDocument.putProperties(map);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error putting : " + e);
            flag = false;
        }

        return flag;

    }

    /**
     * save a failed order in the DB
     *
     * @param database database
     * @param orderMap orderMap
     * @return boolean indicating whether the action was successful or not
     */
    public static boolean saveFailedOrderInDB(Database database, Map<String, Object> orderMap, String comment) {

        Document failedOrdersDocument = database.getDocument(FAILED_ORDERS_DOCUMENT_ID);
        Map<String, Object> map = new HashMap<>();

        boolean flag = true;

        if (failedOrdersDocument.getProperties() != null)
            map.putAll(failedOrdersDocument.getProperties());//put old data from the document in the new map( update )

        orderMap.put(ACTION_DATE, Util.getCurrentDate());
        orderMap.put(SYNC_STATUS, "0");
        orderMap.put(COMMENT, comment);
        //orderMap.put(COMMENT_ID, Util.generateCommentId());

        Document document = database.createDocument();

        try {
            document.putProperties(orderMap);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error putting : " + e);
            flag = false;
        }

        map.put(orderMap.get(ORDER_SHIP_ID).toString(), document.getId());// ordershipid : document_id

        try {
            failedOrdersDocument.putProperties(map);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Error putting : " + e);
            flag = false;
        }

        return flag;

    }

    /**
     * Get all delivered orders from the DB
     *
     * @param database database
     * @return arrayList containing all delivered orders
     */
    public static ArrayList<Map<String, Object>> getAllDeliveredOrders(Database database) {

        Document deliveredOrdersDocument = database.getDocument(DELIVERED_ORDERS_DOCUMENT_ID);
        Map<String, Object> map = deliveredOrdersDocument.getProperties();

        ArrayList<Map<String, Object>> orderMapArrayList = new ArrayList<>();

        if (map != null) {
            Iterator it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                if (pair.getKey().equals("_id") || pair.getKey().equals("_rev"))
                    ;
                else {
                    //System.out.println(pair.getKey() + " = " + pair.getValue());
                    Document document = database.getDocument(pair.getValue().toString());

                    Map<String, Object> orderMap = new HashMap<>();
                    orderMap.put(INCREMENT_ID, document.getProperty(INCREMENT_ID).toString());
                    orderMap.put(ORDER_ID, document.getProperty(ORDER_ID).toString());
                    orderMap.put(CUSTOMER_NAME, document.getProperty(CUSTOMER_NAME).toString());
                    orderMap.put(PHONE, document.getProperty(PHONE).toString());
                    orderMap.put(ADDRESS, document.getProperty(ADDRESS).toString());
                    orderMap.put(CITY, document.getProperty(CITY).toString());
                    orderMap.put(PINCODE, document.getProperty(PINCODE).toString());
                    orderMap.put(METHOD, document.getProperty(METHOD).toString());
                    orderMap.put(TOTAL, document.getProperty(TOTAL).toString());
                    orderMap.put(ORDER_TYPE, document.getProperty(ORDER_TYPE).toString());
                    orderMap.put(ORDER_SHIP_ID, document.getProperty(ORDER_SHIP_ID).toString());
                    orderMap.put(ACCEPT_STATUS, document.getProperty(ACCEPT_STATUS).toString());
                    orderMap.put(ACCEPTED_DATE, document.getProperty(ACCEPTED_DATE).toString());
                    orderMap.put(ACTION_DATE, document.getProperty(ACTION_DATE).toString());
                    orderMap.put(PAY_MODE, document.getProperty(PAY_MODE).toString());
                    orderMap.put(SYNC_STATUS, document.getProperty(SYNC_STATUS).toString());
                    orderMap.put(COMMENT, document.getProperty(COMMENT).toString());
                    //orderMap.put(COMMENT_ID, document.getProperty(COMMENT_ID).toString());

                    orderMapArrayList.add(orderMap);
                }
            }
        }

        return orderMapArrayList;
    }

    /**
     * Get all failed orders from the DB
     *
     * @param database database
     * @return arrayList containing all delivered orders
     */
    public static ArrayList<Map<String, Object>> getAllFailedOrders(Database database) {

        Document failedOrdersDocument = database.getDocument(FAILED_ORDERS_DOCUMENT_ID);
        Map<String, Object> map = failedOrdersDocument.getProperties();

        ArrayList<Map<String, Object>> orderMapArrayList = new ArrayList<>();

        if (map != null) {
            Iterator it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                if (pair.getKey().equals("_id") || pair.getKey().equals("_rev"))
                    ;
                else {
                    //System.out.println(pair.getKey() + " = " + pair.getValue());
                    Document document = database.getDocument(pair.getValue().toString());

                    Map<String, Object> orderMap = new HashMap<>();
                    orderMap.put(INCREMENT_ID, document.getProperty(INCREMENT_ID).toString());
                    orderMap.put(ORDER_ID, document.getProperty(ORDER_ID).toString());
                    orderMap.put(CUSTOMER_NAME, document.getProperty(CUSTOMER_NAME).toString());
                    orderMap.put(PHONE, document.getProperty(PHONE).toString());
                    orderMap.put(ADDRESS, document.getProperty(ADDRESS).toString());
                    orderMap.put(CITY, document.getProperty(CITY).toString());
                    orderMap.put(PINCODE, document.getProperty(PINCODE).toString());
                    orderMap.put(METHOD, document.getProperty(METHOD).toString());
                    orderMap.put(TOTAL, document.getProperty(TOTAL).toString());
                    orderMap.put(ORDER_TYPE, document.getProperty(ORDER_TYPE).toString());
                    orderMap.put(ORDER_SHIP_ID, document.getProperty(ORDER_SHIP_ID).toString());
                    orderMap.put(ACCEPT_STATUS, document.getProperty(ACCEPT_STATUS).toString());
                    orderMap.put(ACCEPTED_DATE, document.getProperty(ACCEPTED_DATE).toString());
                    orderMap.put(ACTION_DATE, document.getProperty(ACTION_DATE).toString());
                    orderMap.put(SYNC_STATUS, document.getProperty(SYNC_STATUS).toString());
                    orderMap.put(COMMENT, document.getProperty(COMMENT).toString());
                    //orderMap.put(COMMENT_ID, document.getProperty(COMMENT_ID).toString());

                    orderMapArrayList.add(orderMap);
                }
            }
        }

        return orderMapArrayList;
    }


    /**
     * Check if an order is present in failed orders
     *
     * @param database    database
     * @param orderShipId orderShipId
     * @return boolean indicating if the order is present in failed orders
     */
    public static boolean checkIfAnOrderIsPresentInFailedOrders(Database database, String orderShipId) {

        Document acceptedOrdersDocument = database.getDocument(FAILED_ORDERS_DOCUMENT_ID);
        Map<String, Object> map = acceptedOrdersDocument.getProperties();

        if (map != null) {
            Iterator it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                if (pair.getKey().equals("_id") || pair.getKey().equals("_rev"))
                    ;
                else {
                    //System.out.println(pair.getKey() + " = " + pair.getValue());
                    if (pair.getKey().toString().equals(orderShipId))
                        return true;

                }
            }
        }

        return false;

    }

    /**
     * delete a failed order from the DB
     *
     * @param database    database
     * @param orderShipId orderShipId
     * @return boolean indicating whether the operation was successful or not
     */
    public static boolean deleteAFailedOrderFromDB(Database database, String orderShipId) {

        boolean flag = true;

        Document failedOrdersDocument = database.getDocument(FAILED_ORDERS_DOCUMENT_ID);
        Map<String, Object> map = new HashMap<>();

        String keyToDelete = null;

        if (failedOrdersDocument.getProperties() != null)
            map.putAll(failedOrdersDocument.getProperties());//put old data from the document in the new map( update )

        if (map != null) {
            Iterator it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                if (pair.getKey().equals("_id") || pair.getKey().equals("_rev"))
                    ;
                else {
                    //System.out.println(pair.getKey() + " = " + pair.getValue());
                    if (orderShipId.equals(pair.getKey().toString())) {

                        keyToDelete = orderShipId;

                        //delete the document
                        Document document = database.getDocument(pair.getValue().toString());

                        try {

                            document.purge();

                        } catch (CouchbaseLiteException e) {

                            Log.d(TAG, "Error while purging : " + e);
                            flag = false;

                        }

                    }

                }
            }
        }

        if (keyToDelete != null) {

            //remove entry from acceptedOrdersDocument
            map.remove(keyToDelete);
            try {

                failedOrdersDocument.putProperties(map);

            } catch (CouchbaseLiteException e) {

                Log.e(TAG, "Error putting : " + e);
                flag = false;

            }
        }

        Log.d(TAG, "failed orders  : " + failedOrdersDocument.getProperties());

        return flag;

    }

    /**
     * Get a failed order from the DB
     *
     * @param database    database
     * @param orderShipId orderShipId
     * @return failed order Map
     */
    public static Map<String, Object> getAFailedOrder(Database database, String orderShipId) {

        Document failedOrdersDocument = database.getDocument(FAILED_ORDERS_DOCUMENT_ID);
        Map<String, Object> map = failedOrdersDocument.getProperties();

        Map<String, Object> orderMap = null;

        if (map != null) {
            Iterator it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                if (pair.getKey().equals("_id") || pair.getKey().equals("_rev"))
                    ;
                else {
                    //System.out.println(pair.getKey() + " = " + pair.getValue());

                    if (orderShipId.equals(pair.getKey().toString())) {

                        //delete the document
                        Document document = database.getDocument(pair.getValue().toString());

                        orderMap = new HashMap<>();
                        orderMap.put(INCREMENT_ID, document.getProperty(INCREMENT_ID).toString());
                        orderMap.put(ORDER_ID, document.getProperty(ORDER_ID).toString());
                        orderMap.put(CUSTOMER_NAME, document.getProperty(CUSTOMER_NAME).toString());
                        orderMap.put(PHONE, document.getProperty(PHONE).toString());
                        orderMap.put(ADDRESS, document.getProperty(ADDRESS).toString());
                        orderMap.put(CITY, document.getProperty(CITY).toString());
                        orderMap.put(PINCODE, document.getProperty(PINCODE).toString());
                        orderMap.put(METHOD, document.getProperty(METHOD).toString());
                        orderMap.put(TOTAL, document.getProperty(TOTAL).toString());
                        orderMap.put(ORDER_TYPE, document.getProperty(ORDER_TYPE).toString());
                        orderMap.put(ORDER_SHIP_ID, document.getProperty(ORDER_SHIP_ID).toString());
                        orderMap.put(ACCEPT_STATUS, document.getProperty(ACCEPT_STATUS).toString());
                        orderMap.put(ACCEPTED_DATE, document.getProperty(ACCEPTED_DATE).toString());
                        orderMap.put(ACTION_DATE, document.getProperty(ACTION_DATE).toString());
                        orderMap.put(SYNC_STATUS, document.getProperty(SYNC_STATUS).toString());
                        orderMap.put(COMMENT, document.getProperty(COMMENT).toString());
                        //orderMap.put(COMMENT_ID, document.getProperty(COMMENT_ID).toString());

                    }
                }
            }
        }

        return orderMap;
    }

    /**
     * get all not synced delivered orders
     *
     * @param database database
     * @return array list of maps containing orders
     */
    public static ArrayList<Map<String, Object>> getAllUnSyncedDeliveredOrders(Database database) {


        Document deliveredOrdersDocument = database.getDocument(DELIVERED_ORDERS_DOCUMENT_ID);
        Map<String, Object> map = deliveredOrdersDocument.getProperties();

        ArrayList<Map<String, Object>> orderMapArrayList = new ArrayList<>();

        if (map != null) {
            Iterator it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                if (pair.getKey().equals("_id") || pair.getKey().equals("_rev"))
                    ;
                else {
                    //System.out.println(pair.getKey() + " = " + pair.getValue());
                    Document document = database.getDocument(pair.getValue().toString());

                    if (document.getProperties().get(SYNC_STATUS).toString().equalsIgnoreCase("0")) {

                        Map<String, Object> orderMap = new HashMap<>();
                        orderMap.put(INCREMENT_ID, document.getProperty(INCREMENT_ID).toString());
                        orderMap.put(ORDER_ID, document.getProperty(ORDER_ID).toString());
                        orderMap.put(CUSTOMER_NAME, document.getProperty(CUSTOMER_NAME).toString());
                        orderMap.put(PHONE, document.getProperty(PHONE).toString());
                        orderMap.put(ADDRESS, document.getProperty(ADDRESS).toString());
                        orderMap.put(CITY, document.getProperty(CITY).toString());
                        orderMap.put(PINCODE, document.getProperty(PINCODE).toString());
                        orderMap.put(METHOD, document.getProperty(METHOD).toString());
                        orderMap.put(TOTAL, document.getProperty(TOTAL).toString());
                        orderMap.put(ORDER_TYPE, document.getProperty(ORDER_TYPE).toString());
                        orderMap.put(ORDER_SHIP_ID, document.getProperty(ORDER_SHIP_ID).toString());
                        orderMap.put(ACCEPT_STATUS, document.getProperty(ACCEPT_STATUS).toString());
                        orderMap.put(ACCEPTED_DATE, document.getProperty(ACCEPTED_DATE).toString());
                        orderMap.put(ACTION_DATE, document.getProperty(ACTION_DATE).toString());
                        orderMap.put(PAY_MODE, document.getProperty(PAY_MODE).toString());
                        orderMap.put(SYNC_STATUS, document.getProperty(SYNC_STATUS).toString());
                        orderMap.put(COMMENT, document.getProperty(COMMENT).toString());
                        //orderMap.put(COMMENT_ID, document.getProperty(COMMENT_ID).toString());

                        orderMapArrayList.add(orderMap);
                    }
                }
            }
        }

        return orderMapArrayList;

    }

    /**
     * update sync status of delivered orders
     *
     * @param database     database
     * @param syncResponse syncResponse
     * @return
     */
    public static boolean updateDeliveredOrdersSyncStatus(Database database, SyncResponse syncResponse) {

        Document acceptedOrdersDocument = database.getDocument(DELIVERED_ORDERS_DOCUMENT_ID);
        Map<String, Object> map = acceptedOrdersDocument.getProperties();

        boolean flag = true;

        if (map != null) {

            Iterator it = map.entrySet().iterator();

            while (it.hasNext()) {

                Map.Entry pair = (Map.Entry) it.next();

                if (pair.getKey().equals("_id") || pair.getKey().equals("_rev"))
                    ;
                else {
                    //System.out.println(pair.getKey() + " = " + pair.getValue());

                    for (String orderShipId : syncResponse.getOrderShipIds()) {

                        if (orderShipId.equals(pair.getKey().toString())) {

                            Document document = database.getDocument(pair.getValue().toString());
                            Map<String, Object> map2 = new HashMap<>();
                            map2.putAll(document.getProperties());
                            map2.put(SYNC_STATUS, "1");

                            try {

                                document.putProperties(map2);

                            } catch (CouchbaseLiteException e) {

                                flag = false;
                                Log.e(TAG, "Error while putting : " + e);

                            }
                        }
                    }
                }
            }
        }

        return flag;

    }

    public static ArrayList<Map<String, Object>> getAllUnSyncedFailedOrders(Database database) {

        Document failedOrdersDocument = database.getDocument(FAILED_ORDERS_DOCUMENT_ID);
        Map<String, Object> map = failedOrdersDocument.getProperties();

        ArrayList<Map<String, Object>> orderMapArrayList = new ArrayList<>();

        if (map != null) {
            Iterator it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                if (pair.getKey().equals("_id") || pair.getKey().equals("_rev"))
                    ;
                else {
                    //System.out.println(pair.getKey() + " = " + pair.getValue());
                    Document document = database.getDocument(pair.getValue().toString());

                    if (document.getProperties().get(SYNC_STATUS).toString().equalsIgnoreCase("0")) {

                        Map<String, Object> orderMap = new HashMap<>();
                        orderMap.put(INCREMENT_ID, document.getProperty(INCREMENT_ID).toString());
                        orderMap.put(ORDER_ID, document.getProperty(ORDER_ID).toString());
                        orderMap.put(CUSTOMER_NAME, document.getProperty(CUSTOMER_NAME).toString());
                        orderMap.put(PHONE, document.getProperty(PHONE).toString());
                        orderMap.put(ADDRESS, document.getProperty(ADDRESS).toString());
                        orderMap.put(CITY, document.getProperty(CITY).toString());
                        orderMap.put(PINCODE, document.getProperty(PINCODE).toString());
                        orderMap.put(METHOD, document.getProperty(METHOD).toString());
                        orderMap.put(TOTAL, document.getProperty(TOTAL).toString());
                        orderMap.put(ORDER_TYPE, document.getProperty(ORDER_TYPE).toString());
                        orderMap.put(ORDER_SHIP_ID, document.getProperty(ORDER_SHIP_ID).toString());
                        orderMap.put(ACCEPT_STATUS, document.getProperty(ACCEPT_STATUS).toString());
                        orderMap.put(ACCEPTED_DATE, document.getProperty(ACCEPTED_DATE).toString());
                        orderMap.put(ACTION_DATE, document.getProperty(ACTION_DATE).toString());
                        orderMap.put(SYNC_STATUS, document.getProperty(SYNC_STATUS).toString());
                        orderMap.put(COMMENT, document.getProperty(COMMENT).toString());
                        //orderMap.put(COMMENT_ID, document.getProperty(COMMENT_ID).toString());

                        orderMapArrayList.add(orderMap);
                    }
                }
            }
        }

        return orderMapArrayList;

    }

    public static boolean updateFailedOrdersSyncStatus(Database database, SyncResponse syncResponse) {

        Document failedOrdersDocument = database.getDocument(FAILED_ORDERS_DOCUMENT_ID);
        Map<String, Object> map = failedOrdersDocument.getProperties();

        boolean flag = true;

        if (map != null) {

            Iterator it = map.entrySet().iterator();

            while (it.hasNext()) {

                Map.Entry pair = (Map.Entry) it.next();

                if (pair.getKey().equals("_id") || pair.getKey().equals("_rev"))
                    ;
                else {
                    //System.out.println(pair.getKey() + " = " + pair.getValue());

                    for (String orderShipId : syncResponse.getOrderShipIds()) {

                        if (orderShipId.equals(pair.getKey().toString())) {

                            Document document = database.getDocument(pair.getValue().toString());
                            Map<String, Object> map2 = new HashMap<>();
                            map2.putAll(document.getProperties());
                            map2.put(SYNC_STATUS, "1");

                            try {

                                document.putProperties(map2);

                            } catch (CouchbaseLiteException e) {

                                flag = false;
                                Log.e(TAG, "Error while putting : " + e);

                            }
                        }
                    }
                }
            }
        }

        return flag;
    }
}
