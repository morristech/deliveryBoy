package com.yoscholar.deliveryboy.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.Database;
import com.yoscholar.deliveryboy.R;
import com.yoscholar.deliveryboy.couchDB.CouchBaseHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

public class OrderDetailsActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final String DELIVERED = "Delivered";
    private static final String FAILED = "Failed";

    private Toolbar toolbar;

    private TextView incrementId;
    private TextView orderShipId;
    private TextView customerName;
    private TextView address;
    private TextView paymentMethod;
    private TextView total;

    private Button deliverySuccessfulButton;
    private Button deliveryFailedButton;

    private FrameLayout orderContainer;
    private LinearLayout exceptionReasonContainer;
    private LinearLayout deliverySuccessfulContainer;

    private RadioGroup deliveryExceptionRadioGroup;
    private EditText otherReasonEditText;
    private Button okButtonFailed;
    private Button cancelButtonFailed;

    private EditText nameOfPersonEditText;
    private Spinner payModeSpinner;
    private Spinner relationSpinner;
    private Button okButtonSuccess;
    private Button cancelButtonSuccess;
    private String[] payModeArray = {"Select Pay Mode", "Prepaid", "Cash", "Card", "Cheque", "Wallet"};
    private String[] relationArray = {"Select Relation", "Self", "Neighbour", "Security"};

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
        getSupportActionBar().setTitle(getIntent().getStringExtra(DeliverOrdersActivity.INCREMENT_ID));

        orderContainer = (FrameLayout) findViewById(R.id.order_container);
        exceptionReasonContainer = (LinearLayout) findViewById(R.id.exception_reason_container);
        deliverySuccessfulContainer = (LinearLayout) findViewById(R.id.successful_delivery_details_container);

        incrementId = (TextView) findViewById(R.id.increment_id);
        orderShipId = (TextView) findViewById(R.id.order_ship_id);
        customerName = (TextView) findViewById(R.id.customer_name);
        address = (TextView) findViewById(R.id.customer_address);
        paymentMethod = (TextView) findViewById(R.id.customer_payment_method);
        total = (TextView) findViewById(R.id.customer_total);

        incrementId.setText(getIntent().getStringExtra(DeliverOrdersActivity.INCREMENT_ID));
        orderShipId.setText(getIntent().getStringExtra(DeliverOrdersActivity.ORDER_SHIP_ID));
        customerName.setText(getIntent().getStringExtra(DeliverOrdersActivity.CUSTOMER_NAME));
        address.setText(getIntent().getStringExtra(DeliverOrdersActivity.CUSTOMER_ADDRESS));
        paymentMethod.setText(getIntent().getStringExtra(DeliverOrdersActivity.CUSTOMER_PAYMENT_METHOD));
        total.setText(getIntent().getStringExtra(DeliverOrdersActivity.CUSTOMER_TOTAL));

        nameOfPersonEditText = (EditText) findViewById(R.id.person_who_collected);
        payModeSpinner = (Spinner) findViewById(R.id.pay_mode_spinner);
        relationSpinner = (Spinner) findViewById(R.id.relation_spinner);

        ArrayAdapter<String> payModeArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, payModeArray);
        payModeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        payModeSpinner.setAdapter(payModeArrayAdapter);

        ArrayAdapter<String> relationArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, relationArray);
        relationArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        relationSpinner.setAdapter(relationArrayAdapter);

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


        okButtonFailed = (Button) findViewById(R.id.ok_button_failed);
        okButtonFailed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RadioButton checkedRadioButton = (RadioButton) findViewById(deliveryExceptionRadioGroup.getCheckedRadioButtonId());

                String reason;//failure reason

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

                        checkTheResponseAndProceed(FAILED, reason);
                    }

                } else {

                    Toast.makeText(OrderDetailsActivity.this, "Reason : " + reason, Toast.LENGTH_SHORT).show();

                    checkTheResponseAndProceed(FAILED, reason);

                }

            }
        });

        cancelButtonFailed = (Button) findViewById(R.id.cancel_button_failed);
        cancelButtonFailed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                orderContainer.setVisibility(View.VISIBLE);
                exceptionReasonContainer.setVisibility(View.GONE);
            }
        });


        okButtonSuccess = (Button) findViewById(R.id.ok_button_success);
        okButtonSuccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean validationSuccessfull = true;

                if (TextUtils.isEmpty(nameOfPersonEditText.getText().toString())) {
                    validationSuccessfull = false;
                    nameOfPersonEditText.setError("This field cannot be empty.");
                } else if (payModeSpinner.getSelectedItemPosition() == 0) {
                    validationSuccessfull = false;
                    ((TextView) payModeSpinner.getSelectedView()).setError("Please select an option.");
                } else if (relationSpinner.getSelectedItemPosition() == 0) {
                    validationSuccessfull = false;
                    ((TextView) relationSpinner.getSelectedView()).setError("Please select an option.");
                }

                if (validationSuccessfull) {

                    checkTheResponseAndProceed(DELIVERED, "Collected by : " + nameOfPersonEditText.getText() + "\n Pay Mode : " + payModeArray[payModeSpinner.getSelectedItemPosition()] + "\n Relation : " + relationArray[relationSpinner.getSelectedItemPosition()]);
                }
            }
        });

        cancelButtonSuccess = (Button) findViewById(R.id.cancel_button_success);
        cancelButtonSuccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                orderContainer.setVisibility(View.VISIBLE);
                deliverySuccessfulContainer.setVisibility(View.GONE);
            }
        });

        deliverySuccessfulButton = (Button) findViewById(R.id.delivered_successfully_button);
        deliverySuccessfulButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getIntent().getStringExtra(DeliverOrdersActivity.CUSTOMER_PAYMENT_METHOD).equals(CouchBaseHelper.PAYMENT_COD))
                    payModeSpinner.setSelection(0);
                else if (getIntent().getStringExtra(DeliverOrdersActivity.CUSTOMER_PAYMENT_METHOD).equals(CouchBaseHelper.PAYMENT_PREPAID))
                    payModeSpinner.setSelection(1);


                orderContainer.setVisibility(View.GONE);
                deliverySuccessfulContainer.setVisibility(View.VISIBLE);

            }
        });

        deliveryFailedButton = (Button) findViewById(R.id.delivery_exception_button);

        if (getIntent().getStringExtra(DeliverOrdersActivity.CALLED_FROM).equals(FailedOrdersActivity.class.getSimpleName())) {

            deliveryFailedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ;
                }
            });

            deliveryFailedButton.setBackgroundResource(R.color.colorAccent);

        } else if (getIntent().getStringExtra(DeliverOrdersActivity.CALLED_FROM).equals(DeliverOrdersActivity.class.getSimpleName())) {

            deliveryFailedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    orderContainer.setVisibility(View.GONE);
                    exceptionReasonContainer.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private void checkTheResponseAndProceed(String status, String comment) {

        Database database = CouchBaseHelper.openCouchBaseDB(OrderDetailsActivity.this);
        Map<String, Object> orderMap = null;

        if (getIntent().getStringExtra(DeliverOrdersActivity.CALLED_FROM).equals(DeliverOrdersActivity.class.getSimpleName())) {

            orderMap = CouchBaseHelper.getAnAcceptedOrder(database, getIntent().getStringExtra(DeliverOrdersActivity.ORDER_SHIP_ID));

        } else if (getIntent().getStringExtra(DeliverOrdersActivity.CALLED_FROM).equals(FailedOrdersActivity.class.getSimpleName())) {

            orderMap = CouchBaseHelper.getAFailedOrder(database, getIntent().getStringExtra(DeliverOrdersActivity.ORDER_SHIP_ID));
        }

        if (status.equals(DELIVERED)) {

            if (orderMap != null)
                CouchBaseHelper.saveDeliveredOrderInDB(database, orderMap, payModeArray[payModeSpinner.getSelectedItemPosition()], comment);

        } else if (status.equals(FAILED)) {

            if (orderMap != null)
                CouchBaseHelper.saveFailedOrderInDB(database, orderMap, comment);

        }

        if (getIntent().getStringExtra(DeliverOrdersActivity.CALLED_FROM).equals(DeliverOrdersActivity.class.getSimpleName())) {
            CouchBaseHelper.deleteAnAcceptedOrderFromDB(database, getIntent().getStringExtra(DeliverOrdersActivity.ORDER_SHIP_ID));
        } else if (getIntent().getStringExtra(DeliverOrdersActivity.CALLED_FROM).equals(FailedOrdersActivity.class.getSimpleName())) {
            CouchBaseHelper.deleteAFailedOrderFromDB(database, getIntent().getStringExtra(DeliverOrdersActivity.ORDER_SHIP_ID));
        }

        setResult(Activity.RESULT_OK);//set result OK
        finish();// finish the activity

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
