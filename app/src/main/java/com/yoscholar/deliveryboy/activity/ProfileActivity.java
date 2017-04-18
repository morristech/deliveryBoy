package com.yoscholar.deliveryboy.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yoscholar.deliveryboy.R;

public class ProfileActivity extends AppCompatActivity {

    private ImageView gif;
    private Toolbar toolbar;

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

    }
}
