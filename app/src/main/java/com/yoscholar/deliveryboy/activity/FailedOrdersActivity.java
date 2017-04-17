package com.yoscholar.deliveryboy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import com.couchbase.lite.Database;
import com.yoscholar.deliveryboy.R;
import com.yoscholar.deliveryboy.adapter.FailedOrdersListViewAdapter;
import com.yoscholar.deliveryboy.couchDB.CouchBaseHelper;
import com.yoscholar.deliveryboy.retrofitPojo.ordersToAccept.Orderdatum;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FailedOrdersActivity extends AppCompatActivity {

    private static final int MY_REQUEST_CODE = 907;
    private Toolbar toolbar;
    private ListView failedOrdersListView;

    private ArrayList<Orderdatum> orderdatumArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_failed_orders);

        init();

        //makeNetworkRequest();

    }

    private void init() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        failedOrdersListView = (ListView) findViewById(R.id.failed_orders_list_view);

        Database database = CouchBaseHelper.openCouchBaseDB(FailedOrdersActivity.this);

        orderdatumArrayList = CouchBaseHelper.getAllFailedOrders(database);

        Collections.sort(orderdatumArrayList, new Comparator<Orderdatum>() {
            @Override
            public int compare(Orderdatum o1, Orderdatum o2) {

                return o1.getIncrementId().compareTo(o2.getIncrementId());
            }
        });

        displayAcceptedOrdersInListView();
    }

    private void displayAcceptedOrdersInListView() {

        FailedOrdersListViewAdapter failedOrdersListViewAdapter = new FailedOrdersListViewAdapter(FailedOrdersActivity.this, orderdatumArrayList);

        failedOrdersListView.setAdapter(failedOrdersListViewAdapter);

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
                Database database = CouchBaseHelper.openCouchBaseDB(FailedOrdersActivity.this);
                orderdatumArrayList = CouchBaseHelper.getAllFailedOrders(database);

                //refresh the list with new data
                displayAcceptedOrdersInListView();

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
    public void onDeliverButtonClick(Orderdatum orderdatum) {

        goToOrderDetailsActivity(orderdatum);
    }

    private void goToOrderDetailsActivity(Orderdatum orderdatum) {
        Intent intent = new Intent(FailedOrdersActivity.this, OrderDetailsActivity.class);

        intent.putExtra(DeliverOrdersActivity.INCREMENT_ID, orderdatum.getIncrementId());
        intent.putExtra(DeliverOrdersActivity.ORDER_ID, orderdatum.getOrderId());
        intent.putExtra(DeliverOrdersActivity.CUSTOMER_NAME, orderdatum.getCustomerName());
        intent.putExtra(DeliverOrdersActivity.CUSTOMER_PHONE, orderdatum.getPhone());
        intent.putExtra(DeliverOrdersActivity.CUSTOMER_ADDRESS, orderdatum.getAddress() + ", " + orderdatum.getCity() + ", " + orderdatum.getPincode());
        intent.putExtra(DeliverOrdersActivity.CUSTOMER_PAYMENT_METHOD, orderdatum.getMethod());
        intent.putExtra(DeliverOrdersActivity.CUSTOMER_TOTAL, orderdatum.getTotal());
        intent.putExtra(DeliverOrdersActivity.ORDER_SHIP_ID, orderdatum.getOrdershipid());
        intent.putExtra(DeliverOrdersActivity.CALLED_FROM, FailedOrdersActivity.class.getSimpleName());

        startActivityForResult(intent, MY_REQUEST_CODE);

    }
}
