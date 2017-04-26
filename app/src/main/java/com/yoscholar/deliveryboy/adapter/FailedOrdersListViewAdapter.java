package com.yoscholar.deliveryboy.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.joanzapata.iconify.widget.IconButton;
import com.yoscholar.deliveryboy.R;
import com.yoscholar.deliveryboy.couchDB.CouchBaseHelper;
import com.yoscholar.deliveryboy.utils.RetrofitApi;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by agrim on 27/2/17.
 */

public class FailedOrdersListViewAdapter extends BaseAdapter {

    private static final int MY_REQUEST_CODE = 967;
    private Context context;
    private ArrayList<Map<String, Object>> orderMapArrayList;
    private ProgressDialog progressDialog;

    public FailedOrdersListViewAdapter(Context context, ArrayList<Map<String, Object>> orderMapArrayList) {
        this.context = context;
        this.orderMapArrayList = orderMapArrayList;
        this.progressDialog = new ProgressDialog(this.context);
        this.progressDialog.setIndeterminate(true);
        this.progressDialog.setMessage("Please wait....");
        this.progressDialog.setCancelable(false);
        this.progressDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public int getCount() {
        return orderMapArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.failed_orders_list_item, parent, false);

        }

        TextView incrementId = (TextView) convertView.findViewById(R.id.increment_id);
        incrementId.setText(orderMapArrayList.get(position).get(CouchBaseHelper.INCREMENT_ID).toString());

        TextView orderShipId = (TextView) convertView.findViewById(R.id.order_ship_id);
        orderShipId.setText(orderMapArrayList.get(position).get(CouchBaseHelper.ORDER_SHIP_ID).toString());

        TextView paymentMethod = (TextView) convertView.findViewById(R.id.pay_mode);
        paymentMethod.setText(orderMapArrayList.get(position).get(CouchBaseHelper.METHOD).toString());

        TextView customerName = (TextView) convertView.findViewById(R.id.customer_name);
        customerName.setText(orderMapArrayList.get(position).get(CouchBaseHelper.CUSTOMER_NAME).toString());

        TextView address = (TextView) convertView.findViewById(R.id.address);
        address.setText(orderMapArrayList.get(position).get(CouchBaseHelper.ADDRESS).toString() + ", " + orderMapArrayList.get(position).get(CouchBaseHelper.CITY).toString() + ", " + orderMapArrayList.get(position).get(CouchBaseHelper.PINCODE).toString());

        IconButton call = (IconButton) convertView.findViewById(R.id.call);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "tel:" + orderMapArrayList.get(position).get(CouchBaseHelper.PHONE).toString();
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse(uri));
                context.startActivity(intent);

            }
        });

        IconButton message = (IconButton) convertView.findViewById(R.id.message);
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(context, "To Do", Toast.LENGTH_SHORT).show();
                String message = "Dear Customer, we tried reaching you to deliver your order " + orderMapArrayList.get(position).get(CouchBaseHelper.INCREMENT_ID).toString() + ". Please call 1860 212 1860 to reschedule the delivery.";
                sendMessage(orderMapArrayList.get(position).get(CouchBaseHelper.PHONE).toString(), message);
            }
        });

        IconButton deliver = (IconButton) convertView.findViewById(R.id.deliver);
        deliver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EventBus.getDefault().post(orderMapArrayList.get(position));

            }
        });

        IconButton route = (IconButton) convertView.findViewById(R.id.route);
        route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String uri = String.format(
                        Locale.ENGLISH,
                        "http://maps.google.com/maps?daddr=%s",
                        orderMapArrayList.get(position).get(CouchBaseHelper.ADDRESS).toString() + ", " + orderMapArrayList.get(position).get(CouchBaseHelper.CITY).toString() + ", " + orderMapArrayList.get(position).get(CouchBaseHelper.PINCODE).toString());

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));

                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");

                context.startActivity(intent);

            }
        });

        IconButton syncIndicator = (IconButton) convertView.findViewById(R.id.sync_indicator);
        if (orderMapArrayList.get(position).get(CouchBaseHelper.SYNC_STATUS).toString().equals("0")) {

            syncIndicator.setText("{ion-close-circled}");

        } else if (orderMapArrayList.get(position).get(CouchBaseHelper.SYNC_STATUS).toString().equals("1")) {

            syncIndicator.setText("{ion-checkmark-circled}");
        }



        /*IconButton reDeliver = (IconButton) convertView.findViewById(R.id.redeliver);
        reDeliver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(context)
                        .setMessage("Are you sure?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with redeliver
                                reDeliverOrder(orderMapArrayList.get(position));

                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();

            }
        });*/

        return convertView;
    }

    /*private void reDeliverOrder(final Orderdatum orderdatum) {

        progressDialog.show();

        RetrofitApi.ApiInterface apiInterface = RetrofitApi.getApiInterfaceInstance();

        Call<ReDeliver> reDeliverCall = apiInterface.redeliver(
                orderdatum.getIncrementId(),//incrementId
                orderdatum.getOrdershipid(),//orderShipId
                AppPreference.getString(context, AppPreference.NAME),//db name
                AppPreference.getString(context, AppPreference.TOKEN)//jwt token
        );

        reDeliverCall.enqueue(new Callback<ReDeliver>() {
            @Override
            public void onResponse(Call<ReDeliver> call, Response<ReDeliver> response) {

                progressDialog.dismiss();

                if (response.isSuccessful()) {

                    if (response.body().getStatus().equalsIgnoreCase("success")) {

                        Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                        Database database = CouchBaseHelper.openCouchBaseDB(context);
                        if (CouchBaseHelper.deleteAFailedOrderFromDB(database, orderdatum.getOrdershipid()))
                            EventBus.getDefault().post(new FailedOrderDeleted(true));

                    } else if (response.body().getStatus().equalsIgnoreCase("failure")) {

                        //show message
                        Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                        EventBus.getDefault().post(new FailedOrderDeleted(false));

                    }

                } else {

                    Toast.makeText(context, "Some error.", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onFailure(Call<ReDeliver> call, Throwable t) {

                progressDialog.dismiss();

                if (context != null)
                    Toast.makeText(context, "Network Problem.", Toast.LENGTH_SHORT).show();

            }
        });

    }*/

    private void sendMessage(String phone, String message) {

        RetrofitApi.ApiInterface apiInterface = RetrofitApi.getApiInterfaceInstance();

        Call<ResponseBody> sendMessageCall = apiInterface.sendMessage(
                "http://trans.kapsystem.com/api/web2sms.php",
                "A3bc8ddacb55a7d989292cb3f5a6e7aa0",
                phone,
                "YOSCLR",
                message
        );

        sendMessageCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful())
                    if (context != null)
                        Toast.makeText(context, "Message request sent successfully.", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (context != null)
                    Toast.makeText(context, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
