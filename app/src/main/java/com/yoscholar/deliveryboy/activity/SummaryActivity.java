package com.yoscholar.deliveryboy.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.couchbase.lite.Database;
import com.joanzapata.iconify.widget.IconButton;
import com.yoscholar.deliveryboy.R;
import com.yoscholar.deliveryboy.couchDB.CouchBaseHelper;
import com.yoscholar.deliveryboy.utils.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

public class SummaryActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private Toolbar toolbar;

    private TextView selectedDateTextView;
    private IconButton dateButton;
    private TextView acceptedCountTextView;
    private TextView deliveredCountTextView;
    private TextView codCountTextView;
    private TextView cashCodCountTextView;
    private TextView cashCollectedTextView;

    private ArrayList<Map<String, Object>> orderMapArrayList;

    private DatePickerDialog datePickerDialog;
    private Calendar calendar = Calendar.getInstance();
    private int year = calendar.get(Calendar.YEAR);
    private int month = calendar.get(Calendar.MONTH);
    private int day = calendar.get(Calendar.DAY_OF_MONTH);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        init();

    }

    private void init() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        acceptedCountTextView = (TextView) findViewById(R.id.accepted_count);
        deliveredCountTextView = (TextView) findViewById(R.id.delivered_count);
        codCountTextView = (TextView) findViewById(R.id.cod_count);
        cashCodCountTextView = (TextView) findViewById(R.id.cash_cod_count);
        cashCollectedTextView = (TextView) findViewById(R.id.cash_collected);

        Database database = CouchBaseHelper.openCouchBaseDB(this);
        orderMapArrayList = new ArrayList<>();
        orderMapArrayList.addAll(CouchBaseHelper.getAllAcceptedOrders(database));
        orderMapArrayList.addAll(CouchBaseHelper.getAllDeliveredOrders(database));
        orderMapArrayList.addAll(CouchBaseHelper.getAllFailedOrders(database));

        selectedDateTextView.setText(Util.getCurrentDate());
        showData(Util.getCurrentDate());

    }

    private void showData(String date) {

        //accepted
        int acceptedCount = 0;

        for (Map<String, Object> orderMap : orderMapArrayList) {

            if (!orderMap.get(CouchBaseHelper.ACCEPTED_DATE).toString().equals(""))
                if (orderMap.get(CouchBaseHelper.ACCEPTED_DATE).toString().equals(date))
                    ++acceptedCount;
        }

        acceptedCountTextView.setText(String.valueOf(acceptedCount));

        //delivered
        int deliveredCount = 0;

        Database database = CouchBaseHelper.openCouchBaseDB(this);

        for (Map<String, Object> orderMap : CouchBaseHelper.getAllDeliveredOrders(database)) {

            if (!orderMap.get(CouchBaseHelper.ACCEPTED_DATE).toString().equals(""))
                if (orderMap.get(CouchBaseHelper.ACCEPTED_DATE).toString().equals(date))
                    ++deliveredCount;
        }

        deliveredCountTextView.setText(String.valueOf(deliveredCount));

        //COD
        int codCount = 0;

        for (Map<String, Object> orderMap : CouchBaseHelper.getAllDeliveredOrders(database)) {

            if (!orderMap.get(CouchBaseHelper.ACCEPTED_DATE).toString().equals(""))
                if (orderMap.get(CouchBaseHelper.ACCEPTED_DATE).toString().equals(date) && orderMap.get(CouchBaseHelper.METHOD).toString().equals(CouchBaseHelper.PAYMENT_COD))
                    ++codCount;
        }

        codCountTextView.setText(String.valueOf(codCount));

        //COD
        int cashCodCount = 0;
        int cashCollected = 0;

        for (Map<String, Object> orderMap : CouchBaseHelper.getAllDeliveredOrders(database)) {

            if (!orderMap.get(CouchBaseHelper.ACCEPTED_DATE).toString().equals(""))
                if (orderMap.get(CouchBaseHelper.ACCEPTED_DATE).toString().equals(date) && orderMap.get(CouchBaseHelper.METHOD).toString().equals(CouchBaseHelper.PAYMENT_COD) &&
                        orderMap.get(CouchBaseHelper.PAY_MODE).equals(CouchBaseHelper.PAY_MODE_CASH)) {
                    ++cashCodCount;
                    cashCollected += Integer.parseInt(orderMap.get(CouchBaseHelper.TOTAL).toString());

                }
        }

        cashCodCountTextView.setText(String.valueOf(cashCodCount));
        cashCollectedTextView.setText(String.valueOf(cashCollected));
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
        showData(date);
    }
}
