package com.yoscholar.deliveryboy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.joanzapata.iconify.widget.IconButton;
import com.yoscholar.deliveryboy.R;

public class OptionsActivity extends AppCompatActivity {

    private IconButton acceptOrdersButton;
    private IconButton deliverOrdersButton;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        init();
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

    }
}
