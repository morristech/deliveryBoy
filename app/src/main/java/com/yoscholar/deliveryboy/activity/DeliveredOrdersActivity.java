package com.yoscholar.deliveryboy.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.couchbase.lite.Database;
import com.yoscholar.deliveryboy.R;
import com.yoscholar.deliveryboy.adapter.AcceptedOrdersListViewAdapter;
import com.yoscholar.deliveryboy.adapter.DeliveredOrdersListViewAdapter;
import com.yoscholar.deliveryboy.couchDB.CouchBaseHelper;
import com.yoscholar.deliveryboy.retrofitPojo.ordersToAccept.Orderdatum;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DeliveredOrdersActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ListView deliveredOrdersListView;

    private ArrayList<Orderdatum> orderdatumArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivered_orders);

        init();

        //makeNetworkRequest();

    }

    private void init() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        deliveredOrdersListView = (ListView) findViewById(R.id.delivered_orders_list_view);

        Database database = CouchBaseHelper.openCouchBaseDB(DeliveredOrdersActivity.this);

        orderdatumArrayList = CouchBaseHelper.getAllDeliveredOrders(database);

        Collections.sort(orderdatumArrayList, new Comparator<Orderdatum>() {
            @Override
            public int compare(Orderdatum o1, Orderdatum o2) {

                return o1.getIncrementId().compareTo(o2.getIncrementId());
            }
        });

        displayAcceptedOrdersInListView();
    }

    private void displayAcceptedOrdersInListView() {

        DeliveredOrdersListViewAdapter deliveredOrdersListViewAdapter = new DeliveredOrdersListViewAdapter(DeliveredOrdersActivity.this, orderdatumArrayList);

        deliveredOrdersListView.setAdapter(deliveredOrdersListViewAdapter);

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
