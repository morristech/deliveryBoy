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
import com.joanzapata.iconify.widget.IconTextView;
import com.yoscholar.deliveryboy.R;
import com.yoscholar.deliveryboy.retrofitPojo.ordersToAccept.Orderdatum;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by agrim on 27/2/17.
 */

public class FailedOrdersListViewAdapter extends BaseAdapter {

    private static final int MY_REQUEST_CODE = 967;
    private Context context;
    private ArrayList<Orderdatum> orderdatumArrayList;

    public FailedOrdersListViewAdapter(Context context, ArrayList<Orderdatum> orderdatumArrayList) {
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
        incrementId.setText("#" + orderdatumArrayList.get(position).getIncrementId() + ", #" + orderdatumArrayList.get(position).getOrdershipid() + ", " + orderdatumArrayList.get(position).getMethod());

        TextView customerName = (TextView) convertView.findViewById(R.id.customer_name);
        customerName.setText(orderdatumArrayList.get(position).getCustomerName());

        TextView address = (TextView) convertView.findViewById(R.id.address);
        address.setText(orderdatumArrayList.get(position).getAddress() + ",\n" +
                orderdatumArrayList.get(position).getCity() + ", " +
                orderdatumArrayList.get(position).getPincode());

        /*TextView paymentMethod = (TextView) convertView.findViewById(R.id.payment_method);
        paymentMethod.setText(orderdatumArrayList.get(position).getMethod());
*/
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

                Toast.makeText(context, "To Do", Toast.LENGTH_SHORT).show();
            }
        });

        IconButton deliver = (IconButton) convertView.findViewById(R.id.deliver);
        deliver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EventBus.getDefault().post(orderdatumArrayList.get(position));

            }
        });

        IconTextView route = (IconTextView) convertView.findViewById(R.id.route);
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
}
