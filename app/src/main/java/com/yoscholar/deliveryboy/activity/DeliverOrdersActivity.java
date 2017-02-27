package com.yoscholar.deliveryboy.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.yoscholar.deliveryboy.R;
import com.yoscholar.deliveryboy.adapter.AcceptedOrdersListViewAdapter;
import com.yoscholar.deliveryboy.retrofitPojo.ordersToAccept.AcceptOrders;
import com.yoscholar.deliveryboy.retrofitPojo.ordersToAccept.Orderdatum;
import com.yoscholar.deliveryboy.utils.AppPreference;
import com.yoscholar.deliveryboy.utils.RetrofitApi;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private ProgressDialog progressDialog;

    private ArrayList<Orderdatum> orderdatumArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        init();

        makeNetworkRequest();

    }

    private void init() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        normalOrdersListView = (ListView) findViewById(R.id.normal_orders_list_view);

        progressDialog = new ProgressDialog(DeliverOrdersActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Please wait....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

    }

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

    private void displayAcceptedOrdersInListView(AcceptOrders acceptOrders) {

        if (acceptOrders.getStatus().equalsIgnoreCase("success")) {

            Toast.makeText(DeliverOrdersActivity.this, acceptOrders.getMessage(), Toast.LENGTH_SHORT).show();

            orderdatumArrayList = new ArrayList<>();

            //get only those orders where accept_status = 1
            for (int i = 0; i < acceptOrders.getOrderdata().size(); ++i) {

                if (acceptOrders.getOrderdata().get(i).getAcceptStatus() == 1)
                    orderdatumArrayList.add(acceptOrders.getOrderdata().get(i));

            }

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

        } else if (acceptOrders.getStatus().equalsIgnoreCase("failure")) {

            //show message
            Toast.makeText(DeliverOrdersActivity.this, acceptOrders.getMessage(), Toast.LENGTH_SHORT).show();

            //logout
            AppPreference.clearPreferencesLogout(DeliverOrdersActivity.this);

            openLoginScreen();

            finish();
        }

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

                progressDialog.show();

                makeNetworkRequest();
            }
        }

    }

}
