package com.yoscholar.deliveryboy.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.couchbase.lite.Database;
import com.yoscholar.deliveryboy.R;
import com.yoscholar.deliveryboy.couchDB.CouchBaseHelper;
import com.yoscholar.deliveryboy.retrofitPojo.ordersToAccept.Orderdatum;
import com.yoscholar.deliveryboy.utils.AppPreference;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private ImageView gif;
    private Toolbar toolbar;

    private TextView deliveryBoyName;
    private TextView deliveryCity;
    private TextView acceptedDeliveries;
    private TextView successfulDeliveries;
    private TextView failedDeliveries;

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
        Glide.with(this)
                .load(R.drawable.delivery_boy)
                .asGif()
                .fitCenter()
                .into(gif);

        deliveryBoyName = (TextView) findViewById(R.id.delivery_boy_name);
        deliveryBoyName.setText(AppPreference.getString(this, AppPreference.NAME));

        deliveryCity = (TextView) findViewById(R.id.city);
        deliveryCity.setText(AppPreference.getString(this, AppPreference.CITY));

        acceptedDeliveries = (TextView) findViewById(R.id.accepted_deliveries);

        successfulDeliveries = (TextView) findViewById(R.id.successful_deliveries);

        failedDeliveries = (TextView) findViewById(R.id.failed_deliveries);

        Database database = CouchBaseHelper.openCouchBaseDB(this);

        ArrayList<Orderdatum> acceptedOrderdatumArrayList = new ArrayList<>();
        acceptedOrderdatumArrayList.addAll(CouchBaseHelper.getAllAcceptedOrders(database));

        ArrayList<Orderdatum> deliveredOrderdatumArrayList = new ArrayList<>();
        deliveredOrderdatumArrayList.addAll(CouchBaseHelper.getAllDeliveredOrders(database));

        ArrayList<Orderdatum> failedOrderdatumArrayList = new ArrayList<>();
        failedOrderdatumArrayList.addAll(CouchBaseHelper.getAllFailedOrders(database));

        acceptedDeliveries.setText(String.valueOf((acceptedOrderdatumArrayList.size() + deliveredOrderdatumArrayList.size() + failedOrderdatumArrayList.size())));

        successfulDeliveries.setText(String.valueOf(deliveredOrderdatumArrayList.size()));

        failedDeliveries.setText(String.valueOf(failedOrderdatumArrayList.size()));
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
