package com.yoscholar.deliveryboy.application;

import android.app.Application;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

/**
 * Created by agrim on 10/2/17.
 */

public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        Iconify.with(new FontAwesomeModule());

    }
}
