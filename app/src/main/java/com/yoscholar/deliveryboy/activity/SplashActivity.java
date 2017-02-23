package com.yoscholar.deliveryboy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.yoscholar.deliveryboy.R;
import com.yoscholar.deliveryboy.utils.AppPreference;

public class SplashActivity extends AppCompatActivity {

    // Splash screen timer
    private static final int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        launch();
    }

    private void launch() {

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to showToast case your app logo / company
             */

            @Override
            public void run() {

                if (AppPreference.getBoolean(SplashActivity.this, AppPreference.IS_LOGGED_IN))
                    startActivity(new Intent(SplashActivity.this, OptionsActivity.class));
                else
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));

                finish();

            }
        }, SPLASH_TIME_OUT);
    }
}
