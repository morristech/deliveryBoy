package com.yoscholar.deliveryboy.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.couchbase.lite.Database;
import com.yoscholar.deliveryboy.R;
import com.yoscholar.deliveryboy.couchDB.CouchBaseHelper;
import com.yoscholar.deliveryboy.retrofitPojo.ordersToAccept.Orderdatum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.view.LineChartView;

public class SummaryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        init();

    }

    private void init() {
        LineChartView chart = (LineChartView) findViewById(R.id.chart);

        chart.setInteractive(true);
        chart.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);
        chart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);

        Database database = CouchBaseHelper.openCouchBaseDB(this);
        ArrayList<Orderdatum> orderdatumArrayList = CouchBaseHelper.getAllDeliveredOrders(database);
        Map<String, Integer> dataSet = new HashMap<>();
        for (int i = 0; i < orderdatumArrayList.size(); ++i) {

            if (dataSet.containsKey(orderdatumArrayList.get(i).getActionDate())) {

                Integer value = dataSet.get(orderdatumArrayList.get(i).getActionDate());
                ++value;
                dataSet.put(orderdatumArrayList.get(i).getActionDate(), value);

            } else {

                dataSet.put(orderdatumArrayList.get(i).getActionDate(), 1);

            }
        }

        Log.d("DATA SET", dataSet.toString());

        List<PointValue> deliveredValues = new ArrayList<PointValue>();
        List<PointValue> failedValues = new ArrayList<PointValue>();
        List<AxisValue> XAxisValues = new ArrayList<AxisValue>();

        XAxisValues.add(new AxisValue(0).setLabel(""));
        deliveredValues.add(new PointValue(0, 0));

        int m = 1;

        if (dataSet != null) {

            Iterator it = dataSet.entrySet().iterator();

            while (it.hasNext()) {

                Toast.makeText(this, "Apple", Toast.LENGTH_SHORT).show();


                Map.Entry pair = (Map.Entry) it.next();

                deliveredValues.add(new PointValue(m, Float.parseFloat(pair.getValue().toString())));

                XAxisValues.add(new AxisValue(m).setLabel(pair.getKey().toString()));

                ++m;

            }

        }

        XAxisValues.add(new AxisValue(m).setLabel(""));
        deliveredValues.add(new PointValue(m, 0));

        List<Line> lines = new ArrayList<Line>();

        Line deliveredLine = new Line(deliveredValues);
        deliveredLine.setColor(Color.parseColor("#76C2AF"));
        deliveredLine.setShape(ValueShape.CIRCLE);
        deliveredLine.setCubic(false);
        deliveredLine.setFilled(false);
        deliveredLine.setHasLabels(true);
        //deliveredLine.setHasLabelsOnlyForSelected(true);
        deliveredLine.setHasLines(true);
        deliveredLine.setHasPoints(true);

        Line failedLine = new Line(deliveredValues);
        failedLine.setColor(Color.parseColor("#000000"));
        failedLine.setShape(ValueShape.CIRCLE);
        failedLine.setCubic(false);
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

        axisX.setName("Days");
        axisX.setHasLines(true);
        axisX.setTextColor(Color.parseColor("#000000"));
        //axisX.setLineColor(Color.parseColor("#76C2AF"));


        data.setAxisXBottom(axisX);
        //data.setAxisYLeft(axisY);

        chart.setLineChartData(data);

    }
}
