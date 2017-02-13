package com.yoscholar.deliveryboy.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.joanzapata.iconify.widget.IconButton;
import com.yoscholar.deliveryboy.R;

import java.util.Locale;

public class OrderDetailsActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private TextView orderId;
    private TextView customerName;
    private TextView address;
    private TextView payMode;
    private TextView total;

    private IconButton routeLookUpButton;
    private IconButton callButton;
    private IconButton deliveredSuccessfullyButton;
    private IconButton deliveryExceptionButton;
    private final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        init();

    }

    private void init() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra(OrdersActivity.INCREMENT_ID));

        orderId = (TextView) findViewById(R.id.increment_id);
        customerName = (TextView) findViewById(R.id.customer_name);
        address = (TextView) findViewById(R.id.customer_address);
        payMode = (TextView) findViewById(R.id.customer_payment_method);
        total = (TextView) findViewById(R.id.customer_total);

        orderId.setText(getIntent().getStringExtra(OrdersActivity.INCREMENT_ID));
        customerName.setText(getIntent().getStringExtra(OrdersActivity.CUSTOMER_NAME));
        address.setText(getIntent().getStringExtra(OrdersActivity.CUSTOMER_ADDRESS));
        payMode.setText(getIntent().getStringExtra(OrdersActivity.CUSTOMER_PAYMENT_METHOD));
        total.setText(getIntent().getStringExtra(OrdersActivity.CUSTOMER_TOTAL));

        routeLookUpButton = (IconButton) findViewById(R.id.route_lookup_button);
        routeLookUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openMapsForNavigation();
            }
        });

        callButton = (IconButton) findViewById(R.id.call_button);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                callTheCustomer();

            }
        });

        deliveredSuccessfullyButton = (IconButton) findViewById(R.id.delivered_successfully_button);
        deliveredSuccessfullyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(OrderDetailsActivity.this, "success", Toast.LENGTH_SHORT).show();
            }
        });

        deliveryExceptionButton = (IconButton) findViewById(R.id.delivery_exception_button);
        deliveryExceptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(OrderDetailsActivity.this, "failure", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void callTheCustomer() {

        //if the permission is not granted already
        if (ContextCompat.checkSelfPermission(OrderDetailsActivity.this, Manifest.permission.CALL_PHONE) != PackageManager
                .PERMISSION_GRANTED) {

            requestPermissionToMakePhoneCalls();

        } else {

            makePhoneCall();

        }

    }

    private void requestPermissionToMakePhoneCalls() {

        ActivityCompat.requestPermissions(OrderDetailsActivity.this, new String[]{Manifest.permission.CALL_PHONE},
                MY_PERMISSIONS_REQUEST_CALL_PHONE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    makePhoneCall();


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permission Denied.", Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }

    private void makePhoneCall() {

        String uri = "tel:" + getIntent().getStringExtra(OrdersActivity.CUSTOMER_PHONE);
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse(uri));
        startActivity(intent);

    }

    private void openMapsForNavigation() {

        String uri = String.format(
                Locale.ENGLISH,
                "http://maps.google.com/maps?daddr=%s",
                getIntent().getStringExtra(OrdersActivity.CUSTOMER_ADDRESS));

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));

        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");

        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                super.onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
