package com.yoscholar.deliveryboy.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.widget.IconButton;
import com.yoscholar.deliveryboy.R;
import com.yoscholar.deliveryboy.utils.AppPreference;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_options_activity, menu);

        menu.findItem(R.id.settings).setIcon(new IconDrawable(this, FontAwesomeIcons.fa_gear)
                .colorRes(android.R.color.white)
                .actionBarSize());

        menu.findItem(R.id.log_out).setIcon(new IconDrawable(this, FontAwesomeIcons.fa_sign_out)
                .colorRes(android.R.color.white)
                .actionBarSize());

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.log_out:

                //logout
                AppPreference.clearPreferencesLogout(OptionsActivity.this);

                Toast.makeText(this, "Logged out successfully.", Toast.LENGTH_SHORT).show();

                //open login screen
                openLoginScreen();

                finish();

                return true;

            case R.id.settings:

                startActivity(new Intent(OptionsActivity.this, SettingsActivity.class));

                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void openLoginScreen() {

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }
}
