package com.yoscholar.deliveryboy.activity;

import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.Database;
import com.google.gson.Gson;
import com.yoscholar.deliveryboy.R;
import com.yoscholar.deliveryboy.couchDB.CouchBaseHelper;
import com.yoscholar.deliveryboy.utils.AppPreference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = ProfileActivity.class.getSimpleName();
    private ImageView gif;
    private Toolbar toolbar;

    private TextView deliveryBoyName;
    private TextView deliveryCity;
    private TextView acceptedDeliveries;
    private TextView successfulDeliveries;
    private TextView failedDeliveries;


    private ArrayList<Map<String, Object>> acceptedOrderMapArrayList = new ArrayList<>();
    private ArrayList<Map<String, Object>> deliveredOrderMapArrayList = new ArrayList<>();
    private ArrayList<Map<String, Object>> failedOrderMapArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        init();
    }

    private void init() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        gif = (ImageView) findViewById(R.id.gif);

        deliveryBoyName = (TextView) findViewById(R.id.delivery_boy_name);
        deliveryBoyName.setText(AppPreference.getString(this, AppPreference.NAME));

        deliveryCity = (TextView) findViewById(R.id.city);
        deliveryCity.setText(AppPreference.getString(this, AppPreference.CITY));

        acceptedDeliveries = (TextView) findViewById(R.id.accepted_deliveries);

        successfulDeliveries = (TextView) findViewById(R.id.successful_deliveries);

        failedDeliveries = (TextView) findViewById(R.id.failed_deliveries);

        Database database = CouchBaseHelper.openCouchBaseDB(this);

        acceptedOrderMapArrayList.addAll(CouchBaseHelper.getAllAcceptedOrders(database));
        deliveredOrderMapArrayList.addAll(CouchBaseHelper.getAllDeliveredOrders(database));
        failedOrderMapArrayList.addAll(CouchBaseHelper.getAllFailedOrders(database));

        acceptedDeliveries.setText(String.valueOf((acceptedOrderMapArrayList.size() + deliveredOrderMapArrayList.size() + failedOrderMapArrayList.size())));

        successfulDeliveries.setText(String.valueOf(deliveredOrderMapArrayList.size()));

        failedDeliveries.setText(String.valueOf(failedOrderMapArrayList.size()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_profile_activity, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;

            case R.id.dump:
                try {
                    dumpData();
                } catch (IOException e) {
                    Log.e(TAG, "Error while dumping data :  " + e);
                    Toast.makeText(this, "Data dumping failed.", Toast.LENGTH_SHORT).show();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void dumpData() throws IOException {

        Gson gson = new Gson();

        File direct = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/YoScholarDeliveryLogs");

        if (!direct.exists()) {
            if (direct.mkdir()) // if directory is created
                Log.d(TAG, "YoScholarDeliveryLogs Folder Created.");

        }

        String timeStamp = new SimpleDateFormat("dd-MM-yyyy'T'-HH-mm-ss", Locale.getDefault()).format(new Date());
        String fileName = timeStamp + ".txt";

        File acceptedFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/YoScholarDeliveryLogs" + File.separator + "Accepted_" + fileName);
        File deliveredFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/YoScholarDeliveryLogs" + File.separator + "Delivered_" + fileName);
        File failedFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/YoScholarDeliveryLogs" + File.separator + "Failed_" + fileName);

        if (acceptedFile.createNewFile()) {

            if (acceptedFile.exists()) {

                OutputStream fo = new FileOutputStream(acceptedFile);
                fo.write(gson.toJson(acceptedOrderMapArrayList).getBytes());
                fo.close();
                Log.d(TAG, "Accepted File Created in YoScholarDeliveryLogs Folder.");

            }

        }

        MediaScannerConnection.scanFile(this, new String[]{acceptedFile.getAbsolutePath()}, null, null);

        if (deliveredFile.createNewFile()) {

            if (deliveredFile.exists()) {

                OutputStream fo = new FileOutputStream(deliveredFile);
                fo.write(gson.toJson(deliveredOrderMapArrayList).getBytes());
                fo.close();
                Log.d(TAG, "Delivered File Created in YoScholarDeliveryLogs Folder.");

            }

        }

        MediaScannerConnection.scanFile(this, new String[]{deliveredFile.getAbsolutePath()}, null, null);

        if (failedFile.createNewFile()) {

            if (failedFile.exists()) {

                OutputStream fo = new FileOutputStream(failedFile);
                fo.write(gson.toJson(failedOrderMapArrayList).getBytes());
                fo.close();
                Log.d(TAG, "Failed File Created in YoScholarDeliveryLogs Folder.");

            }

        }

        MediaScannerConnection.scanFile(this, new String[]{failedFile.getAbsolutePath()}, null, null);

        Toast.makeText(this, "Data dumped successfully.", Toast.LENGTH_SHORT).show();

    }


}
