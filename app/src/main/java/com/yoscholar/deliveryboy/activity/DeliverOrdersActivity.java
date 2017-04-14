package com.yoscholar.deliveryboy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.couchbase.lite.Database;
import com.yoscholar.deliveryboy.R;
import com.yoscholar.deliveryboy.adapter.AcceptedOrdersListViewAdapter;
import com.yoscholar.deliveryboy.couchDB.CouchBaseHelper;
import com.yoscholar.deliveryboy.retrofitPojo.ordersToAccept.Orderdatum;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DeliverOrdersActivity extends AppCompatActivity {

    public static final String INCREMENT_ID = "increment_id";
    public static final String ORDER_ID = "order_id";
    public static final String CUSTOMER_NAME = "customer_name";
    public static final String CUSTOMER_PHONE = "customer_phone";
    public static final String CUSTOMER_ADDRESS = "customer_address";
    public static final String CUSTOMER_PAYMENT_METHOD = "customer_payment_method";
    public static final String CUSTOMER_TOTAL = "customer_total";
    public static final String ORDER_SHIP_ID = "order_ship_id";
    private static final int MY_REQUEST_CODE = 200;

    private Toolbar toolbar;
    private ListView normalOrdersListView;

    private ArrayList<Orderdatum> orderdatumArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        init();

        //makeNetworkRequest();

    }

    private void init() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        normalOrdersListView = (ListView) findViewById(R.id.normal_orders_list_view);

        Database database = CouchBaseHelper.openCouchBaseDB(DeliverOrdersActivity.this);

        orderdatumArrayList = CouchBaseHelper.getAllAcceptedOrders(database);

        Collections.sort(orderdatumArrayList, new Comparator<Orderdatum>() {
            @Override
            public int compare(Orderdatum o1, Orderdatum o2) {

                return o1.getIncrementId().compareTo(o2.getIncrementId());
            }
        });

        displayAcceptedOrdersInListView();
    }

/*
    private void makeNetworkRequest() {

        RetrofitApi.ApiInterface apiInterface = RetrofitApi.getApiInterfaceInstance();

        Call<AcceptOrders> normalOrdersCall = apiInterface.ordersToAccept(
                AppPreference.getString(DeliverOrdersActivity.this, AppPreference.NAME),//db name
                AppPreference.getString(DeliverOrdersActivity.this, AppPreference.TOKEN)//jwt token
        );

        normalOrdersCall.enqueue(new Callback<AcceptOrders>() {

            @Override
            public void onResponse(Call<AcceptOrders> call, Response<AcceptOrders> response) {

                progressDialog.dismiss();

                if (response.isSuccessful()) {

                    displayAcceptedOrdersInListView(response.body());

                } else {

                    Toast.makeText(DeliverOrdersActivity.this, "Some Error", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onFailure(Call<AcceptOrders> call, Throwable t) {

                progressDialog.dismiss();
                Toast.makeText(DeliverOrdersActivity.this, "Network problem", Toast.LENGTH_SHORT).show();
                //Log.d("VICKY", call.request().url().toString());

            }
        });
    }
*/

    private void displayAcceptedOrdersInListView() {

        AcceptedOrdersListViewAdapter acceptedOrdersListViewAdapter = new AcceptedOrdersListViewAdapter(DeliverOrdersActivity.this, orderdatumArrayList);

        normalOrdersListView.setAdapter(acceptedOrdersListViewAdapter);
        normalOrdersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(DeliverOrdersActivity.this, OrderDetailsActivity.class);

                intent.putExtra(INCREMENT_ID, orderdatumArrayList.get(position).getIncrementId());
                intent.putExtra(ORDER_ID, orderdatumArrayList.get(position).getOrderId());
                intent.putExtra(CUSTOMER_NAME, orderdatumArrayList.get(position).getCustomerName());
                intent.putExtra(CUSTOMER_PHONE, orderdatumArrayList.get(position).getPhone());
                intent.putExtra(CUSTOMER_ADDRESS, orderdatumArrayList.get(position).getAddress() + ", " + orderdatumArrayList.get(position).getCity() + ", " + orderdatumArrayList.get(position).getPincode());
                intent.putExtra(CUSTOMER_PAYMENT_METHOD, orderdatumArrayList.get(position).getMethod());
                intent.putExtra(CUSTOMER_TOTAL, orderdatumArrayList.get(position).getTotal());
                intent.putExtra(ORDER_SHIP_ID, orderdatumArrayList.get(position).getOrdershipid());

                startActivityForResult(intent, MY_REQUEST_CODE);

            }
        });

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

                Toast.makeText(this, "Caught", Toast.LENGTH_SHORT).show();
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


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBackGroundOrdersSyncFinished(Orderdatum orderdatum) {

        goToOrderDetailsActivity(orderdatum);
    }

    private void goToOrderDetailsActivity(Orderdatum orderdatum) {
        Intent intent = new Intent(DeliverOrdersActivity.this, OrderDetailsActivity.class);

        intent.putExtra(DeliverOrdersActivity.INCREMENT_ID, orderdatum.getIncrementId());
        intent.putExtra(DeliverOrdersActivity.ORDER_ID, orderdatum.getOrderId());
        intent.putExtra(DeliverOrdersActivity.CUSTOMER_NAME, orderdatum.getCustomerName());
        intent.putExtra(DeliverOrdersActivity.CUSTOMER_PHONE, orderdatum.getPhone());
        intent.putExtra(DeliverOrdersActivity.CUSTOMER_ADDRESS, orderdatum.getAddress() + ", " + orderdatum.getCity() + ", " + orderdatum.getPincode());
        intent.putExtra(DeliverOrdersActivity.CUSTOMER_PAYMENT_METHOD, orderdatum.getMethod());
        intent.putExtra(DeliverOrdersActivity.CUSTOMER_TOTAL, orderdatum.getTotal());
        intent.putExtra(DeliverOrdersActivity.ORDER_SHIP_ID, orderdatum.getOrdershipid());

        startActivityForResult(intent, MY_REQUEST_CODE);

    }
}
