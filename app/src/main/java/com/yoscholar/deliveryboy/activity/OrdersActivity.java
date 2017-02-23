package com.yoscholar.deliveryboy.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
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
    public static final String ORDER_ID = "order_id";
    public static final String CUSTOMER_NAME = "customer_name";
    public static final String CUSTOMER_PHONE = "customer_phone";
    public static final String CUSTOMER_ADDRESS = "customer_address";
    public static final String CUSTOMER_PAYMENT_METHOD = "customer_payment_method";
    public static final String CUSTOMER_TOTAL = "customer_total";
    private static final int MY_REQUEST_CODE = 200;

    private Toolbar toolbar;
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
                AppPreference.getString(OrdersActivity.this, AppPreference.NAME),//db name
                AppPreference.getString(OrdersActivity.this, AppPreference.TOKEN)//jwt token
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
                    intent.putExtra(ORDER_ID, normalOrders.getOrderdata().get(position).getOrderId());
                    intent.putExtra(CUSTOMER_NAME, normalOrders.getOrderdata().get(position).getCustomerName());
                    intent.putExtra(CUSTOMER_PHONE, normalOrders.getOrderdata().get(position).getPhone());
                    intent.putExtra(CUSTOMER_ADDRESS, normalOrders.getOrderdata().get(position).getAddress() + ", " + normalOrders.getOrderdata().get(position).getCity() + ", " + normalOrders.getOrderdata().get(position).getPincode());
                    intent.putExtra(CUSTOMER_PAYMENT_METHOD, normalOrders.getOrderdata().get(position).getMethod());
                    intent.putExtra(CUSTOMER_TOTAL, normalOrders.getOrderdata().get(position).getTotal());

                    startActivityForResult(intent, MY_REQUEST_CODE);

                }
            });

        } else if (normalOrders.getStatus().equalsIgnoreCase("failure")) {

            //show message
            Toast.makeText(OrdersActivity.this, normalOrders.getMessage(), Toast.LENGTH_SHORT).show();

            //logout
            AppPreference.clearPreferencesLogout(OrdersActivity.this);

            openLoginScreen();

            finish();
        }

    }

    private void openLoginScreen() {

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_orders_activity, menu);

        menu.findItem(R.id.settings).setIcon(new IconDrawable(this, FontAwesomeIcons.fa_gear)
                .colorRes(android.R.color.white)
                .actionBarSize());

        menu.findItem(R.id.log_out).setIcon(new IconDrawable(this, FontAwesomeIcons.fa_sign_out)
                .colorRes(android.R.color.white)
                .actionBarSize());

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.log_out:

                //logout
                AppPreference.clearPreferencesLogout(OrdersActivity.this);

                Toast.makeText(this, "Logged out successfully.", Toast.LENGTH_SHORT).show();

                //open login screen
                openLoginScreen();

                return true;

            case R.id.settings:

                startActivity(new Intent(OrdersActivity.this, SettingsActivity.class));

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
