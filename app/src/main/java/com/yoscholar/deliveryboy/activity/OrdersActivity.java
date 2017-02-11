package com.yoscholar.deliveryboy.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.yoscholar.deliveryboy.R;
import com.yoscholar.deliveryboy.adapter.NormalOrdersListViewAdapter;
import com.yoscholar.deliveryboy.retrofitPojo.normalOrders.NormalOrders;
import com.yoscholar.deliveryboy.utils.AppPreference;
import com.yoscholar.deliveryboy.utils.RetrofitApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrdersActivity extends AppCompatActivity {

    public static final String INCREMENT_ID = "increment_id";
    public static final String CUSTOMER_NAME = "customer_name";
    public static final String CUSTOMER_PHONE = "customer_phone";
    public static final String CUSTOMER_ADDRESS = "customer_address";
    public static final String CUSTOMER_PAYMENT_METHOD = "customer_payment_method";
    public static final String CUSTOMER_TOTAL = "customer_payment_method";


    private Toolbar toolbar;
    private Button openMapsButton;
    private ListView normalOrdersListView;
    private ProgressDialog progressDialog;

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

        openMapsButton = (Button) findViewById(R.id.open_maps);
        openMapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + Uri.encode("129, 18th Cross Road, Rajiv Gandhi Nagar, HSR Layout, Bengaluru, Karnataka"));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);

            }
        });

        normalOrdersListView = (ListView) findViewById(R.id.normal_orders_list_view);

        progressDialog = new ProgressDialog(OrdersActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Please wait....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

    }

    private void makeNetworkRequest() {

        RetrofitApi.ApiInterface apiInterface = RetrofitApi.getApiInterfaceInstance();

        Call<NormalOrders> normalOrdersCall = apiInterface.normalOrders(
                AppPreference.getString(OrdersActivity.this, AppPreference.NAME),
                AppPreference.getString(OrdersActivity.this, AppPreference.TOKEN)
        );

        normalOrdersCall.enqueue(new Callback<NormalOrders>() {

            @Override
            public void onResponse(Call<NormalOrders> call, Response<NormalOrders> response) {

                progressDialog.dismiss();

                if (response.isSuccessful()) {

                    displayNormalOrdersInListView(response.body());

                } else {
                    Toast.makeText(OrdersActivity.this, "Some Error", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<NormalOrders> call, Throwable t) {

                progressDialog.dismiss();
                Toast.makeText(OrdersActivity.this, "Network problem", Toast.LENGTH_SHORT).show();
                //Log.d("VICKY", call.request().url().toString());

            }
        });
    }

    private void displayNormalOrdersInListView(final NormalOrders normalOrders) {

        if (normalOrders.getStatus().equalsIgnoreCase("success")) {

            Toast.makeText(OrdersActivity.this, normalOrders.getMessage(), Toast.LENGTH_SHORT).show();

            NormalOrdersListViewAdapter normalOrdersListViewAdapter = new NormalOrdersListViewAdapter(OrdersActivity.this, normalOrders);

            normalOrdersListView.setAdapter(normalOrdersListViewAdapter);
            normalOrdersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent intent = new Intent(OrdersActivity.this, OrderDetailsActivity.class);
                    intent.putExtra(INCREMENT_ID, normalOrders.getOrderdata().get(position).getIncrementId());
                    intent.putExtra(CUSTOMER_NAME, normalOrders.getOrderdata().get(position).getCustomerName());
                    intent.putExtra(CUSTOMER_PHONE, normalOrders.getOrderdata().get(position).getPhone());
                    intent.putExtra(CUSTOMER_ADDRESS, normalOrders.getOrderdata().get(position).getAddress() + ", " + normalOrders.getOrderdata().get(position).getCity() + ", " + normalOrders.getOrderdata().get(position).getPincode());
                    intent.putExtra(CUSTOMER_PAYMENT_METHOD, normalOrders.getOrderdata().get(position).getMethod());
                    intent.putExtra(CUSTOMER_TOTAL, normalOrders.getOrderdata().get(position).getTotal());
                    startActivity(intent);

                }
            });

        } else if (normalOrders.getStatus().equalsIgnoreCase("failure")) {

            Toast.makeText(OrdersActivity.this, normalOrders.getMessage(), Toast.LENGTH_SHORT).show();

        }

    }

}
