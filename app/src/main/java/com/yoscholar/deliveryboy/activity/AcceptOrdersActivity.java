package com.yoscholar.deliveryboy.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Toast;

import com.yoscholar.deliveryboy.R;
import com.yoscholar.deliveryboy.adapter.AcceptOrdersListViewAdapter;
import com.yoscholar.deliveryboy.pojo.OrderAccepted;
import com.yoscholar.deliveryboy.retrofitPojo.ordersToAccept.AcceptOrders;
import com.yoscholar.deliveryboy.utils.AppPreference;
import com.yoscholar.deliveryboy.utils.RetrofitApi;
import com.yoscholar.deliveryboy.utils.Util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AcceptOrdersActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = AcceptOrdersActivity.class.getSimpleName();
    private Toolbar toolbar;
    private ListView acceptOrdersListView;
    private AcceptOrdersListViewAdapter acceptOrdersListViewAdapter;

    private ProgressDialog progressDialog;

    private DatePickerDialog datePickerDialog;
    private Calendar calendar = Calendar.getInstance();
    private int year = calendar.get(Calendar.YEAR);
    private int month = calendar.get(Calendar.MONTH);
    private int day = calendar.get(Calendar.DAY_OF_MONTH);

    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_orders);

        init();

        getOrdersToAccept(Util.dateForApi(calendar.getTime()));
    }

    private void init() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        datePickerDialog = new DatePickerDialog(this, this, year, month, day);
        datePickerDialog.setCanceledOnTouchOutside(false);
        datePickerDialog.setCancelable(false);

        acceptOrdersListView = (ListView) findViewById(R.id.orders_list_to_accept);

        progressDialog = new ProgressDialog(AcceptOrdersActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Please wait....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    private void getOrdersToAccept(String date) {

        progressDialog.show();

        Log.d(TAG, "Requesting");

        RetrofitApi.ApiInterface apiInterface = RetrofitApi.getApiInterfaceInstance();

        Call<AcceptOrders> acceptOrdersCall = apiInterface.ordersToAccept(
                AppPreference.getString(AcceptOrdersActivity.this, AppPreference.NAME),//db name
                AppPreference.getString(AcceptOrdersActivity.this, AppPreference.TOKEN),//jwt token
                date
        );

        acceptOrdersCall.enqueue(new Callback<AcceptOrders>() {

            @Override
            public void onResponse(Call<AcceptOrders> call, Response<AcceptOrders> response) {

                progressDialog.dismiss();

                if (response.isSuccessful()) {

                    displayOrdersToAcceptInListView(response.body());

                } else {

                    Toast.makeText(AcceptOrdersActivity.this, "Some Error", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onFailure(Call<AcceptOrders> call, Throwable t) {

                progressDialog.dismiss();
                Toast.makeText(AcceptOrdersActivity.this, "Network problem", Toast.LENGTH_SHORT).show();
                //Log.d("VICKY", call.request().url().toString());
                //Log.d(TAG, t.toString());

            }
        });
    }

    private void displayOrdersToAcceptInListView(AcceptOrders acceptOrders) {

        if (acceptOrders.getStatus().equalsIgnoreCase("success")) {

            Toast.makeText(AcceptOrdersActivity.this, acceptOrders.getMessage(), Toast.LENGTH_SHORT).show();

            acceptOrdersListViewAdapter = new AcceptOrdersListViewAdapter(AcceptOrdersActivity.this, acceptOrders);

            acceptOrdersListView.setAdapter(acceptOrdersListViewAdapter);

        } else if (acceptOrders.getStatus().equalsIgnoreCase("failure")) {

            //show message
            Toast.makeText(AcceptOrdersActivity.this, acceptOrders.getMessage(), Toast.LENGTH_SHORT).show();

            //logout
            AppPreference.clearPreferencesLogout(AcceptOrdersActivity.this);

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
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_accept_orders_activity, menu);

        this.menu = menu;
        menu.findItem(R.id.date_picker).setTitle(Util.getCurrentDate());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;

            case R.id.date_picker:
                datePickerDialog.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);

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
    public void onOrderAccepted(OrderAccepted orderAccepted) {

        if (orderAccepted.isAccepted()) {

            progressDialog.show();

            getOrdersToAccept(Util.dateForApi(calendar.getTime()));

        } else {

            //logout
            AppPreference.clearPreferencesLogout(AcceptOrdersActivity.this);

            //open login screen
            openLoginScreen();

            finish();
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calendar.set(year, month, dayOfMonth);

        SimpleDateFormat format = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH);
        String date = format.format(calendar.getTime());

        menu.findItem(R.id.date_picker).setTitle(date);

        Toast.makeText(this, "Api Date : " + Util.dateForApi(calendar.getTime()), Toast.LENGTH_SHORT).show();

        getOrdersToAccept(Util.dateForApi(calendar.getTime()));
    }
}
