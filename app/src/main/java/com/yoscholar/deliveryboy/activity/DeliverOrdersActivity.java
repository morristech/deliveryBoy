package com.yoscholar.deliveryboy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import com.couchbase.lite.Database;
import com.yoscholar.deliveryboy.R;
import com.yoscholar.deliveryboy.adapter.AcceptedOrdersListViewAdapter;
import com.yoscholar.deliveryboy.couchDB.CouchBaseHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

public class DeliverOrdersActivity extends AppCompatActivity {

    public static final String INCREMENT_ID = "increment_id";
    public static final String ORDER_ID = "order_id";
    public static final String CUSTOMER_NAME = "customer_name";
    public static final String CUSTOMER_PHONE = "customer_phone";
    public static final String CUSTOMER_ADDRESS = "customer_address";
    public static final String CUSTOMER_PAYMENT_METHOD = "customer_payment_method";
    public static final String CUSTOMER_TOTAL = "customer_total";
    public static final String ORDER_SHIP_ID = "order_ship_id";
    public static final String CALLED_FROM = "called_from";
    private static final int MY_REQUEST_CODE = 200;
    private static final String TAG = DeliverOrdersActivity.class.getSimpleName();

    private Toolbar toolbar;
    private ListView normalOrdersListView;

    private ArrayList<Map<String, Object>> orderMapArrayList = new ArrayList<>();
    AcceptedOrdersListViewAdapter acceptedOrdersListViewAdapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliver_orders);

        init();

        //makeNetworkRequest();

    }

    private void init() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        normalOrdersListView = (ListView) findViewById(R.id.normal_orders_list_view);

        Database database = CouchBaseHelper.openCouchBaseDB(DeliverOrdersActivity.this);

        orderMapArrayList = CouchBaseHelper.getAllAcceptedOrders(database);

        Collections.sort(orderMapArrayList, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {

                return o1.get(CouchBaseHelper.INCREMENT_ID).toString().compareTo(o2.get(CouchBaseHelper.INCREMENT_ID).toString());

            }

        });

        displayAcceptedOrdersInListView();
    }

    private void displayAcceptedOrdersInListView() {

        acceptedOrdersListViewAdapter = new AcceptedOrdersListViewAdapter(DeliverOrdersActivity.this, orderMapArrayList);

        normalOrdersListView.setAdapter(acceptedOrdersListViewAdapter);

    }

    private void openLoginScreen() {

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == MY_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {

                //get new data from db
                Database database = CouchBaseHelper.openCouchBaseDB(DeliverOrdersActivity.this);
                orderMapArrayList = CouchBaseHelper.getAllAcceptedOrders(database);

                //refresh the list with new data
                displayAcceptedOrdersInListView();

                Log.d(TAG, "data : " + orderMapArrayList);

                // Toast.makeText(this, "Caught", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    //called form the AcceptedOrdersListViewAdapter
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeliverButtonClick(Map<String, Object> orderMap) {

        goToOrderDetailsActivity(orderMap);
    }

    private void goToOrderDetailsActivity(Map<String, Object> orderMap) {
        Intent intent = new Intent(DeliverOrdersActivity.this, OrderDetailsActivity.class);

        intent.putExtra(DeliverOrdersActivity.INCREMENT_ID, orderMap.get(CouchBaseHelper.INCREMENT_ID).toString());
        intent.putExtra(DeliverOrdersActivity.ORDER_ID, orderMap.get(CouchBaseHelper.ORDER_ID).toString());
        intent.putExtra(DeliverOrdersActivity.CUSTOMER_NAME, orderMap.get(CouchBaseHelper.CUSTOMER_NAME).toString());
        intent.putExtra(DeliverOrdersActivity.CUSTOMER_PHONE, orderMap.get(CouchBaseHelper.PHONE).toString());
        intent.putExtra(DeliverOrdersActivity.CUSTOMER_ADDRESS, orderMap.get(CouchBaseHelper.ADDRESS) + ", " + orderMap.get(CouchBaseHelper.CITY) + ", " + orderMap.get(CouchBaseHelper.PINCODE));
        intent.putExtra(DeliverOrdersActivity.CUSTOMER_PAYMENT_METHOD, orderMap.get(CouchBaseHelper.METHOD).toString());
        intent.putExtra(DeliverOrdersActivity.CUSTOMER_TOTAL, orderMap.get(CouchBaseHelper.TOTAL).toString());
        intent.putExtra(DeliverOrdersActivity.ORDER_SHIP_ID, orderMap.get(CouchBaseHelper.ORDER_SHIP_ID).toString());
        intent.putExtra(DeliverOrdersActivity.CALLED_FROM, DeliverOrdersActivity.class.getSimpleName());

        startActivityForResult(intent, MY_REQUEST_CODE);

    }
}
