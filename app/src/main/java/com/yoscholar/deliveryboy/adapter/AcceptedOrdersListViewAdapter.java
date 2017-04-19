package com.yoscholar.deliveryboy.adapter;

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
import com.yoscholar.deliveryboy.retrofitPojo.ordersToAccept.Orderdatum;
import com.yoscholar.deliveryboy.utils.RetrofitApi;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by agrim on 27/2/17.
 */

public class AcceptedOrdersListViewAdapter extends BaseAdapter {

    private static final int MY_REQUEST_CODE = 967;
    private Context context;
    private ArrayList<Orderdatum> orderdatumArrayList;

    public AcceptedOrdersListViewAdapter(Context context, ArrayList<Orderdatum> orderdatumArrayList) {
        this.context = context;
        this.orderdatumArrayList = orderdatumArrayList;
    }

    @Override
    public int getCount() {
        return orderdatumArrayList.size();
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
            convertView = inflater.inflate(R.layout.accepted_orders_list_item, parent, false);

        }

        TextView incrementId = (TextView) convertView.findViewById(R.id.increment_id);
        incrementId.setText(orderdatumArrayList.get(position).getIncrementId());

        TextView orderShipId = (TextView) convertView.findViewById(R.id.order_ship_id);
        orderShipId.setText(orderdatumArrayList.get(position).getOrdershipid());

        TextView paymentMethod = (TextView) convertView.findViewById(R.id.payment_method);
        paymentMethod.setText(orderdatumArrayList.get(position).getMethod());

        TextView customerName = (TextView) convertView.findViewById(R.id.customer_name);
        customerName.setText(orderdatumArrayList.get(position).getCustomerName());

        TextView address = (TextView) convertView.findViewById(R.id.address);
        address.setText(orderdatumArrayList.get(position).getAddress() + ", " + orderdatumArrayList.get(position).getCity() + ", " + orderdatumArrayList.get(position).getPincode());

        IconButton call = (IconButton) convertView.findViewById(R.id.call);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "tel:" + orderdatumArrayList.get(position).getPhone();
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
                String message = "Dear Customer, Your Order " + orderdatumArrayList.get(position).getIncrementId() + " could not be delivered. Please call 1860 212 1860 to reschedule the delivery.";
                sendMessage(orderdatumArrayList.get(position).getPhone(), message);
            }
        });

        IconButton deliver = (IconButton) convertView.findViewById(R.id.deliver);
        deliver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EventBus.getDefault().post(orderdatumArrayList.get(position));

            }
        });

        IconButton route = (IconButton) convertView.findViewById(R.id.route);
        route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String uri = String.format(
                        Locale.ENGLISH,
                        "http://maps.google.com/maps?daddr=%s",
                        orderdatumArrayList.get(position).getAddress() + ", " + orderdatumArrayList.get(position).getCity() + ", " + orderdatumArrayList.get(position).getPincode());

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));

                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");

                context.startActivity(intent);

            }
        });

        return convertView;
    }

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
