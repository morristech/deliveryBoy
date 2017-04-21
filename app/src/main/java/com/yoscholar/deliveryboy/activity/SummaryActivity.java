package com.yoscholar.deliveryboy.activity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.couchbase.lite.Database;
import com.joanzapata.iconify.widget.IconButton;
import com.yoscholar.deliveryboy.R;
import com.yoscholar.deliveryboy.couchDB.CouchBaseHelper;
import com.yoscholar.deliveryboy.retrofitPojo.ordersToAccept.Orderdatum;
import com.yoscholar.deliveryboy.utils.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.view.LineChartView;

public class SummaryActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private Toolbar toolbar;

    private TextView selectedDateTextView;
    private IconButton dateButton;
    private TextView acceptedCountTextView;
    private TextView deliveredCountTextView;
    private TextView codCountTextView;
    private TextView cashCodCountTextView;
    private TextView cashCollectedTextView;

    private ArrayList<Orderdatum> orderdatumArrayList;

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
        orderdatumArrayList = new ArrayList<>();
        orderdatumArrayList.addAll(CouchBaseHelper.getAllAcceptedOrders(database));
        orderdatumArrayList.addAll(CouchBaseHelper.getAllDeliveredOrders(database));
        orderdatumArrayList.addAll(CouchBaseHelper.getAllFailedOrders(database));

        selectedDateTextView.setText(Util.getCurrentDate());
        showData(Util.getCurrentDate());

    }

    private void showData(String date) {

        //accepted
        int acceptedCount = 0;

        for (Orderdatum orderdatum : orderdatumArrayList) {

            if (!orderdatum.getAcceptedDate().equals(""))
                if (orderdatum.getAcceptedDate().equals(date))
                    ++acceptedCount;
        }

        acceptedCountTextView.setText(String.valueOf(acceptedCount));

        //delivered
        int deliveredCount = 0;

        Database database = CouchBaseHelper.openCouchBaseDB(this);

        for (Orderdatum orderdatum : CouchBaseHelper.getAllDeliveredOrders(database)) {

            if (!orderdatum.getAcceptedDate().equals(""))
                if (orderdatum.getAcceptedDate().equals(date))
                    ++deliveredCount;
        }

        deliveredCountTextView.setText(String.valueOf(deliveredCount));

        //COD
        int codCount = 0;

        for (Orderdatum orderdatum : CouchBaseHelper.getAllDeliveredOrders(database)) {

            if (!orderdatum.getAcceptedDate().equals(""))
                if (orderdatum.getAcceptedDate().equals(date) && orderdatum.getMethod().equals(CouchBaseHelper.PAYMENT_COD))
                    ++codCount;
        }

        codCountTextView.setText(String.valueOf(codCount));

        //COD
        int cashCodCount = 0;
        int cashCollected = 0;

        for (Orderdatum orderdatum : CouchBaseHelper.getAllDeliveredOrders(database)) {

            if (!orderdatum.getAcceptedDate().equals(""))
                if (orderdatum.getAcceptedDate().equals(date) && orderdatum.getMethod().equals(CouchBaseHelper.PAYMENT_COD) &&
                        orderdatum.getPayMode().equals(CouchBaseHelper.PAY_MODE_CASH)) {
                    ++cashCodCount;
                    cashCollected += Integer.parseInt(orderdatum.getTotal());

                }
        }

        cashCodCountTextView.setText(String.valueOf(cashCodCount));
        cashCollectedTextView.setText(String.valueOf(cashCollected));
    }


    private void drawLineChart() {

        LineChartView chart = (LineChartView) findViewById(R.id.chart);
        chart.setInteractive(true);
        chart.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);
        chart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);

        Database database = CouchBaseHelper.openCouchBaseDB(this);

        ArrayList<Orderdatum> orderdatumArrayList = new ArrayList<>();

        orderdatumArrayList.addAll(CouchBaseHelper.getAllDeliveredOrders(database));
        orderdatumArrayList.addAll(CouchBaseHelper.getAllFailedOrders(database));

        ArrayList<String> datesStringArrayList = new ArrayList<>();

        for (Orderdatum orderdatum : orderdatumArrayList) {

            if (!datesStringArrayList.contains(orderdatum.getActionDate()))
                datesStringArrayList.add(orderdatum.getActionDate());

        }

        for (String d : datesStringArrayList) {
            Log.d("DATE", "Date : " + d);
        }

        ArrayList<Date> datesArrayList = new ArrayList<>();

        for (String dateString : datesStringArrayList) {

            try {

                Date date = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH).parse(dateString);

                datesArrayList.add(date);

            } catch (ParseException e) {

                e.printStackTrace();

            }

        }

        Collections.sort(datesArrayList, new Comparator<Date>() {
            @Override
            public int compare(Date o1, Date o2) {
                return o1.compareTo(o2);
            }
        });

        List<PointValue> deliveredValues = new ArrayList<PointValue>();
        List<PointValue> failedValues = new ArrayList<PointValue>();
        List<AxisValue> XAxisValues = new ArrayList<AxisValue>();
        List<AxisValue> YAxisValues = new ArrayList<AxisValue>();

        XAxisValues.add(new AxisValue(0).setLabel(""));
        deliveredValues.add(new PointValue(0, 0));
        failedValues.add(new PointValue(0, 0));

        int m = 1;

        int range = 0;

        for (Date date : datesArrayList) {

            String dateString = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH).format(date);

            int c1 = 0;

            for (Orderdatum orderdatum : CouchBaseHelper.getAllDeliveredOrders(database)) {

                if (orderdatum.getActionDate().equals(dateString)) {
                    ++c1;
                }
            }

            deliveredValues.add(new PointValue(m, c1));


            int c2 = 0;

            for (Orderdatum orderdatum : CouchBaseHelper.getAllFailedOrders(database)) {

                if (orderdatum.getActionDate().equals(dateString)) {
                    ++c2;
                }
            }

            if (c1 > range)
                range = c1;

            if (c2 > range)
                range = c2;

            failedValues.add(new PointValue(m, c2));

            XAxisValues.add(new AxisValue(m).setLabel(dateString));

            ++m;
        }

        XAxisValues.add(new AxisValue(m).setLabel(""));
        deliveredValues.add(new PointValue(m, 0));
        failedValues.add(new PointValue(m, 0));

        for (int i = 0; i <= range; ++i) {

            YAxisValues.add(new AxisValue(i).setLabel(String.valueOf(i)));

        }

        List<Line> lines = new ArrayList<Line>();

        Line deliveredLine = new Line(deliveredValues);
        deliveredLine.setColor(Color.parseColor("#76C2AF"));
        deliveredLine.setShape(ValueShape.CIRCLE);
        deliveredLine.setCubic(true);
        deliveredLine.setFilled(false);
        deliveredLine.setHasLabels(true);
        //deliveredLine.setHasLabelsOnlyForSelected(true);
        deliveredLine.setHasLines(true);
        deliveredLine.setHasPoints(true);
        deliveredLine.setPointRadius(10);

        Line failedLine = new Line(failedValues);
        failedLine.setColor(Color.parseColor("#000000"));
        failedLine.setShape(ValueShape.CIRCLE);
        failedLine.setCubic(true);
        failedLine.setFilled(false);
        failedLine.setHasLabels(true);
        //failedLine.setHasLabelsOnlyForSelected(true);
        failedLine.setHasLines(true);
        failedLine.setHasPoints(true);

        lines.add(deliveredLine);
        lines.add(failedLine);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        Axis axisX = new Axis(XAxisValues);
        axisX.setHasLines(true);
        axisX.setLineColor(Color.parseColor("#acaba6"));
        axisX.setTextColor(Color.parseColor("#acaba6"));

        Axis axisY = new Axis(YAxisValues);
        axisY.setHasLines(true);
        axisY.setLineColor(Color.parseColor("#acaba6"));
        axisY.setTextColor(Color.parseColor("#acaba6"));

        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);
        chart.setLineChartData(data);
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
