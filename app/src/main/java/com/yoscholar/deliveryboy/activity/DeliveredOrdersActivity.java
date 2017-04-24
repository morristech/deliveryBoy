package com.yoscholar.deliveryboy.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.couchbase.lite.Database;
import com.joanzapata.iconify.widget.IconButton;
import com.yoscholar.deliveryboy.R;
import com.yoscholar.deliveryboy.adapter.DeliveredOrdersListViewAdapter;
import com.yoscholar.deliveryboy.couchDB.CouchBaseHelper;
import com.yoscholar.deliveryboy.utils.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

public class DeliveredOrdersActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private Toolbar toolbar;
    private ListView deliveredOrdersListView;

    private ArrayList<Map<String, Object>> orderMapArrayList = new ArrayList<>();

    private DatePickerDialog datePickerDialog;
    private Calendar calendar = Calendar.getInstance();
    private int year = calendar.get(Calendar.YEAR);
    private int month = calendar.get(Calendar.MONTH);
    private int day = calendar.get(Calendar.DAY_OF_MONTH);

    private TextView selectedDateTextView;
    private IconButton dateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivered_orders);

        init();

        //makeNetworkRequest();

    }

    private void init() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Database database = CouchBaseHelper.openCouchBaseDB(this);
        orderMapArrayList = CouchBaseHelper.getAllDeliveredOrders(database);

        deliveredOrdersListView = (ListView) findViewById(R.id.delivered_orders_list_view);

        datePickerDialog = new DatePickerDialog(this, this, year, month, day);
        datePickerDialog.setCanceledOnTouchOutside(false);
        datePickerDialog.setCancelable(false);

        selectedDateTextView = (TextView) findViewById(R.id.selected_date);
        dateButton = (IconButton) findViewById(R.id.date_button);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        selectedDateTextView.setText(Util.getCurrentDate());
        displayAcceptedOrdersInListView(Util.getCurrentDate());
    }

    private void displayAcceptedOrdersInListView(String date) {

        ArrayList<Map<String, Object>> filteredOrderMapArrayList = new ArrayList<>();

        for (Map<String, Object> orderMap : orderMapArrayList) {

            if (!orderMap.get(CouchBaseHelper.ACCEPTED_DATE).toString().equals(""))
                if (orderMap.get(CouchBaseHelper.ACCEPTED_DATE).toString().equals(date))
                    filteredOrderMapArrayList.add(orderMap);
        }

        DeliveredOrdersListViewAdapter deliveredOrdersListViewAdapter = new DeliveredOrdersListViewAdapter(DeliveredOrdersActivity.this, filteredOrderMapArrayList);

        deliveredOrdersListView.setAdapter(deliveredOrdersListViewAdapter);

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
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        calendar.set(year, month, dayOfMonth);

        SimpleDateFormat format = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH);
        String date = format.format(calendar.getTime());

        selectedDateTextView.setText(date);

        displayAcceptedOrdersInListView(date);
    }
}
