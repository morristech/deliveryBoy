package com.yoscholar.deliveryboy.activity;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.yoscholar.deliveryboy.R;
import com.yoscholar.deliveryboy.retrofitPojo.updateOrder.UpdateOrder;
import com.yoscholar.deliveryboy.utils.AppPreference;
import com.yoscholar.deliveryboy.utils.RetrofitApi;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailsActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final String DELIVERED = "Delivered";
    private static final String RE_DELIVER = "Re-Deliver";

    private Toolbar toolbar;

    private TextView orderId;
    private TextView customerName;
    private TextView address;
    private TextView payMode;
    private TextView total;

    private Button deliverySuccessfulButton;
    private Button deliveryExceptionButton;
    private final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 100;

    private ProgressDialog progressDialog;

    private FrameLayout orderContainer;
    private LinearLayout exceptionReasonContainer;

    private RadioGroup deliveryExceptionRadioGroup;
    private EditText otherReasonEditText;
    private Button okButton;
    private Button cancelButton;

    private DatePickerDialog datePickerDialog;
    private Calendar calendar = Calendar.getInstance();
    private int year = calendar.get(Calendar.YEAR);
    private int month = calendar.get(Calendar.MONTH);
    private int day = calendar.get(Calendar.DAY_OF_MONTH);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        init();

    }

    private void init() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra(DeliverOrdersActivity.INCREMENT_ID));

        orderContainer = (FrameLayout) findViewById(R.id.order_container);
        exceptionReasonContainer = (LinearLayout) findViewById(R.id.exception_reason_container);

        progressDialog = new ProgressDialog(OrderDetailsActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Please wait....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        orderId = (TextView) findViewById(R.id.increment_id);
        customerName = (TextView) findViewById(R.id.customer_name);
        address = (TextView) findViewById(R.id.customer_address);
        payMode = (TextView) findViewById(R.id.customer_payment_method);
        total = (TextView) findViewById(R.id.customer_total);

        orderId.setText(getIntent().getStringExtra(DeliverOrdersActivity.INCREMENT_ID));
        customerName.setText(getIntent().getStringExtra(DeliverOrdersActivity.CUSTOMER_NAME));
        address.setText(getIntent().getStringExtra(DeliverOrdersActivity.CUSTOMER_ADDRESS));
        payMode.setText(getIntent().getStringExtra(DeliverOrdersActivity.CUSTOMER_PAYMENT_METHOD));
        total.setText(getIntent().getStringExtra(DeliverOrdersActivity.CUSTOMER_TOTAL));

        otherReasonEditText = (EditText) findViewById(R.id.other_reason_edit_text);

        datePickerDialog = new DatePickerDialog(OrderDetailsActivity.this, OrderDetailsActivity.this, year, month, day);
        datePickerDialog.setCanceledOnTouchOutside(false);
        datePickerDialog.setCancelable(false);
        datePickerDialog.setMessage("Select Future Delivery Date : ");
        datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, "", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);


        deliveryExceptionRadioGroup = (RadioGroup) findViewById(R.id.delivery_exception_radio_group);
        deliveryExceptionRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.door_locked:
                        otherReasonEditText.setText("");
                        otherReasonEditText.setVisibility(View.GONE);
                        break;

                    case R.id.future_delivery_requested:
                        otherReasonEditText.setText("");
                        otherReasonEditText.setVisibility(View.VISIBLE);

                        datePickerDialog.show();

                        break;

                    case R.id.unable_to_collect_cash:
                        otherReasonEditText.setText("");
                        otherReasonEditText.setVisibility(View.GONE);
                        break;

                    case R.id.customer_not_accepting_package:
                        otherReasonEditText.setText("");
                        otherReasonEditText.setVisibility(View.GONE);
                        break;

                    case R.id.unable_to_go:
                        otherReasonEditText.setText("");
                        otherReasonEditText.setVisibility(View.GONE);
                        break;

                    case R.id.other:
                        otherReasonEditText.setText("");
                        otherReasonEditText.setVisibility(View.VISIBLE);
                        break;

                    default:
                        otherReasonEditText.setText("");
                        otherReasonEditText.setVisibility(View.GONE);
                        break;
                }
            }
        });


        okButton = (Button) findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RadioButton checkedRadioButton = (RadioButton) findViewById(deliveryExceptionRadioGroup.getCheckedRadioButtonId());

                String reason;

                if (!TextUtils.isEmpty(otherReasonEditText.getText().toString()))
                    reason = checkedRadioButton.getText().toString() + " : " + otherReasonEditText.getText().toString();
                else
                    reason = checkedRadioButton.getText().toString();

                if (deliveryExceptionRadioGroup.getCheckedRadioButtonId() == R.id.future_delivery_requested ||
                        deliveryExceptionRadioGroup.getCheckedRadioButtonId() == R.id.other) {

                    if (TextUtils.isEmpty(otherReasonEditText.getText().toString())) {

                        otherReasonEditText.setError("Please fill this field.");

                    } else {

                        Toast.makeText(OrderDetailsActivity.this, "Reason : " + reason, Toast.LENGTH_SHORT).show();
                        progressDialog.show();
                        updateOrder(RE_DELIVER, reason);
                    }

                } else {

                    Toast.makeText(OrderDetailsActivity.this, "Reason : " + reason, Toast.LENGTH_SHORT).show();
                    progressDialog.show();
                    updateOrder(RE_DELIVER, reason);

                }

            }
        });

        cancelButton = (Button) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                orderContainer.setVisibility(View.VISIBLE);
                exceptionReasonContainer.setVisibility(View.GONE);
            }
        });

        deliverySuccessfulButton = (Button) findViewById(R.id.delivered_successfully_button);
        deliverySuccessfulButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(OrderDetailsActivity.this)
                        .setMessage("Are you sure?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                progressDialog.show();
                                updateOrder(DELIVERED, "");

                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();

            }
        });

        deliveryExceptionButton = (Button) findViewById(R.id.delivery_exception_button);
        deliveryExceptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                orderContainer.setVisibility(View.GONE);
                exceptionReasonContainer.setVisibility(View.VISIBLE);
            }
        });
    }

    private void callTheCustomer() {

        //if the permission is not granted already
        if (ContextCompat.checkSelfPermission(OrderDetailsActivity.this, Manifest.permission.CALL_PHONE) != PackageManager
                .PERMISSION_GRANTED) {

            requestPermissionToMakePhoneCalls();

        } else {

            makePhoneCall();

        }

    }

    private void requestPermissionToMakePhoneCalls() {

        ActivityCompat.requestPermissions(OrderDetailsActivity.this, new String[]{Manifest.permission.CALL_PHONE},
                MY_PERMISSIONS_REQUEST_CALL_PHONE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted
                    makePhoneCall();

                } else {

                    // permission denied
                    Toast.makeText(this, "Permission Denied.", Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }

    private void makePhoneCall() {

        String uri = "tel:" + getIntent().getStringExtra(DeliverOrdersActivity.CUSTOMER_PHONE);
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse(uri));
        startActivity(intent);

    }

    private void openMapsForNavigation() {

        String uri = String.format(
                Locale.ENGLISH,
                "http://maps.google.com/maps?daddr=%s",
                getIntent().getStringExtra(DeliverOrdersActivity.CUSTOMER_ADDRESS));

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));

        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");

        startActivity(intent);
    }

    private void updateOrder(String status, String comment) {

        RetrofitApi.ApiInterface apiInterface = RetrofitApi.getApiInterfaceInstance();

        Call<UpdateOrder> updateOrderCall = apiInterface.updateOrder(
                getIntent().getStringExtra(DeliverOrdersActivity.INCREMENT_ID),//increment id
                comment,//comment
                getIntent().getStringExtra(DeliverOrdersActivity.ORDER_SHIP_ID),//order ship id
                status,// Delivered / Re-Deliver
                AppPreference.getString(OrderDetailsActivity.this, AppPreference.NAME),//db name
                AppPreference.getString(OrderDetailsActivity.this, AppPreference.TOKEN)//jwt token
        );

        updateOrderCall.enqueue(new Callback<UpdateOrder>() {

            @Override
            public void onResponse(Call<UpdateOrder> call, Response<UpdateOrder> response) {

                progressDialog.dismiss();

                if (response.isSuccessful()) {

                    checkTheResponseAndProceed(response.body());

                } else {

                    Toast.makeText(OrderDetailsActivity.this, "Some Error", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onFailure(Call<UpdateOrder> call, Throwable t) {

                progressDialog.dismiss();
                Toast.makeText(OrderDetailsActivity.this, "Network problem", Toast.LENGTH_SHORT).show();
                //Log.d("VICKY", call.request().url().toString());

            }
        });
    }

    private void checkTheResponseAndProceed(UpdateOrder updateOrder) {

        if (updateOrder.getStatus().equalsIgnoreCase("success")) {

            Toast.makeText(this, updateOrder.getMessage(), Toast.LENGTH_SHORT).show();
            setResult(Activity.RESULT_OK);//set result OK
            finish();// finish the activity

        } else if (updateOrder.getStatus().equalsIgnoreCase("failure")) {

            Toast.makeText(this, updateOrder.getMessage(), Toast.LENGTH_SHORT).show();

            //logout
            AppPreference.clearPreferencesLogout(OrderDetailsActivity.this);

            //open login screen
            openLoginScreen();

            finish();// finish the current activity

        }

    }

    private void openLoginScreen() {

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

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

        SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy");
        String strDate = format.format(calendar.getTime());

        otherReasonEditText.setText(strDate);
    }
}
