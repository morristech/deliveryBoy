package com.yoscholar.deliveryboy.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.couchbase.lite.Database;
import com.yoscholar.deliveryboy.R;
import com.yoscholar.deliveryboy.adapter.FailedOrdersListViewAdapter;
import com.yoscholar.deliveryboy.couchDB.CouchBaseHelper;
import com.yoscholar.deliveryboy.pojo.FailedOrderDeleted;
import com.yoscholar.deliveryboy.retrofitPojo.getShipIdsStatus.Stat;
import com.yoscholar.deliveryboy.retrofitPojo.getShipIdsStatus.Status;
import com.yoscholar.deliveryboy.retrofitPojo.ordersToAccept.Orderdatum;
import com.yoscholar.deliveryboy.utils.AppPreference;
import com.yoscholar.deliveryboy.utils.RetrofitApi;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FailedOrdersActivity extends AppCompatActivity {

    private static final int MY_REQUEST_CODE = 907;
    private Toolbar toolbar;
    private ListView failedOrdersListView;
    private ProgressDialog progressDialog;


    private ArrayList<Orderdatum> orderdatumArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_failed_orders);

        init();

    }

    private void init() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        failedOrdersListView = (ListView) findViewById(R.id.failed_orders_list_view);

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Please wait....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);


        JSONArray orderShipIdsJsonArray = new JSONArray();
        Database database = CouchBaseHelper.openCouchBaseDB(FailedOrdersActivity.this);

        for (Orderdatum orderdatum : CouchBaseHelper.getAllFailedOrders(database))
            orderShipIdsJsonArray.put(orderdatum.getOrdershipid());

        if (orderShipIdsJsonArray.length() != 0)
            getShipIdsStatus(orderShipIdsJsonArray);
        else
            displayFailedOrdersInListView();
    }

    public void getShipIdsStatus(JSONArray orderShipIdsJsonArray) {

        progressDialog.show();

        RetrofitApi.ApiInterface apiInterface = RetrofitApi.getApiInterfaceInstance();
        Call<Status> statusCall = apiInterface.getShipIdsStatus(
                orderShipIdsJsonArray.toString(),
                AppPreference.getString(this, AppPreference.TOKEN)//jwt token
        );

        statusCall.enqueue(new Callback<Status>() {
            @Override
            public void onResponse(Call<Status> call, Response<Status> response) {

                progressDialog.dismiss();

                if (response.isSuccessful()) {

                    if (response.body().getStatus().equalsIgnoreCase("success")) {

                        Database database = CouchBaseHelper.openCouchBaseDB(FailedOrdersActivity.this);

                        for (Stat stat : response.body().getStats()) {

                            if (stat.getRstatus().equals("1"))
                                CouchBaseHelper.deleteAFailedOrderFromDB(database, stat.getOrderShipId());

                        }

                        displayFailedOrdersInListView();

                    } else if (response.body().getStatus().equalsIgnoreCase("failure")) {

                        //show message
                        Toast.makeText(FailedOrdersActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                        //logout
                        AppPreference.clearPreferencesLogout(FailedOrdersActivity.this);

                        //open login screen
                        openLoginScreen();

                        finish();

                    }

                } else {

                    Toast.makeText(FailedOrdersActivity.this, "Some Error.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Status> call, Throwable t) {

                progressDialog.dismiss();

                Toast.makeText(FailedOrdersActivity.this, "Network Error.", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void displayFailedOrdersInListView() {

        Database database = CouchBaseHelper.openCouchBaseDB(this);
        orderdatumArrayList = CouchBaseHelper.getAllFailedOrders(database);

        Collections.sort(orderdatumArrayList, new Comparator<Orderdatum>() {
            @Override
            public int compare(Orderdatum o1, Orderdatum o2) {

                return o1.getIncrementId().compareTo(o2.getIncrementId());
            }
        });

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
                displayFailedOrdersInListView();

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


    //called form the FailedOrdersListViewAdapter
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOrderRedelivered(FailedOrderDeleted failedOrderDeleted) {

        if (failedOrderDeleted.isDeleted()) {

            //get new data from db
            Database database = CouchBaseHelper.openCouchBaseDB(FailedOrdersActivity.this);
            orderdatumArrayList = CouchBaseHelper.getAllFailedOrders(database);

            //refresh the list with new data
            displayFailedOrdersInListView();

        } else {

            //logout
            AppPreference.clearPreferencesLogout(FailedOrdersActivity.this);

            //open login screen
            openLoginScreen();

            finish();
        }

    }

    private void openLoginScreen() {

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

}
