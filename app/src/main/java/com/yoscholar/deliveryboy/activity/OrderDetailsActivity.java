package com.yoscholar.deliveryboy.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.yoscholar.deliveryboy.R;

public class OrderDetailsActivity extends AppCompatActivity {


    private TextView orderId;
    private TextView customerName;
    private TextView address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
    }
}
