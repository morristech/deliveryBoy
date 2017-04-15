package com.yoscholar.deliveryboy.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import com.couchbase.lite.Database;
import com.yoscholar.deliveryboy.R;
import com.yoscholar.deliveryboy.adapter.DeliveredOrdersListViewAdapter;
import com.yoscholar.deliveryboy.couchDB.CouchBaseHelper;
import com.yoscholar.deliveryboy.retrofitPojo.ordersToAccept.Orderdatum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FailedOrdersActivity extends AppCompatActivity {

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

        DeliveredOrdersListViewAdapter deliveredOrdersListViewAdapter = new DeliveredOrdersListViewAdapter(FailedOrdersActivity.this, orderdatumArrayList);

        failedOrdersListView.setAdapter(deliveredOrdersListViewAdapter);

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

}
