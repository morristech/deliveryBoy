package com.yoscholar.deliveryboy.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.joanzapata.iconify.widget.IconButton;
import com.yoscholar.deliveryboy.R;
import com.yoscholar.deliveryboy.couchDB.CouchBaseHelper;
import com.yoscholar.deliveryboy.service.DeliveredOrdersSyncService;
import com.yoscholar.deliveryboy.service.FailedOrdersShipIdsStatusService;
import com.yoscholar.deliveryboy.utils.AppPreference;
import com.yoscholar.deliveryboy.utils.Util;


public class OptionsActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_CODE = 900;
    private static final String TAG = "OptionsActivity";
    private IconButton acceptOrdersButton;
    private IconButton deliverOrdersButton;
    private IconButton deliveredButton;
    private IconButton failedButton;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        init();

        requestPermissionToMakePhoneCalls();
    }

    private void init() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        acceptOrdersButton = (IconButton) findViewById(R.id.accept_orders_button);
        acceptOrdersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(OptionsActivity.this, AcceptOrdersActivity.class));
            }
        });

        deliverOrdersButton = (IconButton) findViewById(R.id.deliver_orders_button);
        deliverOrdersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(OptionsActivity.this, DeliverOrdersActivity.class));
            }
        });

        deliveredButton = (IconButton) findViewById(R.id.delivered_button);
        deliveredButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(OptionsActivity.this, DeliveredOrdersActivity.class));
            }
        });

        failedButton = (IconButton) findViewById(R.id.failed_button);
        failedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(OptionsActivity.this, FailedOrdersActivity.class));

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_options_activity, menu);

        menu.findItem(R.id.summary).setIcon(new IconDrawable(this, IoniconsIcons.ion_android_clipboard)
                .colorRes(android.R.color.white)
                .actionBarSize());

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.summary:

                startActivity(new Intent(OptionsActivity.this, SummaryActivity.class));

                return true;

            case R.id.profile:

                startActivity(new Intent(OptionsActivity.this, ProfileActivity.class));

                return true;

            case R.id.settings:

                startActivity(new Intent(OptionsActivity.this, SettingsActivity.class));

                return true;

            case R.id.log_out:

                //logout
                AppPreference.clearPreferencesLogout(OptionsActivity.this);

                Toast.makeText(this, "Logged out successfully.", Toast.LENGTH_SHORT).show();

                //open login screen
                openLoginScreen();

                finish();

                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void openLoginScreen() {

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    private void requestPermissionToMakePhoneCalls() {

        ActivityCompat.requestPermissions(OptionsActivity.this, new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    //Toast.makeText(this, "Permission Granted.", Toast.LENGTH_SHORT).show();

                    // permission was granted

                } else {

                    // permission denied
                    Toast.makeText(this, "Permission Denied, cannot continue.", Toast.LENGTH_SHORT).show();
                    finish();
                }

                return;
            }

        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (Util.isOnline(this)) {

            //to prevent the annoying crash
            if (CouchBaseHelper.openCouchBaseDB(this) != null) {

                //start service to sync delivered orders
                if (!AppPreference.getBoolean(this, AppPreference.IS_DELIVERED_ORDERS_SYNC_SERVICE_RUNNING))
                    startService(new Intent(OptionsActivity.this, DeliveredOrdersSyncService.class));

                //start service to check status of ship ids of failed order'
                Log.d(TAG, "Is failed orders ship ids status service running : " + AppPreference.getBoolean(this, AppPreference.IS_FAILED_ORDERS_SHIP_IDS_STATUS_SERVICE_RUNNING));
                if (!AppPreference.getBoolean(this, AppPreference.IS_FAILED_ORDERS_SHIP_IDS_STATUS_SERVICE_RUNNING))
                    startService(new Intent(OptionsActivity.this, FailedOrdersShipIdsStatusService.class));
            }
        }
    }

}
