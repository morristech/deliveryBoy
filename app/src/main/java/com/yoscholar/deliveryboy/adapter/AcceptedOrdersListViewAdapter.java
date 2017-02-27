package com.yoscholar.deliveryboy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yoscholar.deliveryboy.R;
import com.yoscholar.deliveryboy.retrofitPojo.ordersToAccept.Orderdatum;

import java.util.ArrayList;

/**
 * Created by agrim on 27/2/17.
 */

public class AcceptedOrdersListViewAdapter extends BaseAdapter {

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
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.accepted_orders_list_item, parent, false);

        }

        TextView incrementId = (TextView) convertView.findViewById(R.id.increment_id);
        incrementId.setText("#" + orderdatumArrayList.get(position).getIncrementId());

        TextView address = (TextView) convertView.findViewById(R.id.address);
        address.setText(orderdatumArrayList.get(position).getAddress() + ",\n" +
                orderdatumArrayList.get(position).getCity() + ", " +
                orderdatumArrayList.get(position).getPincode());

        TextView paymentMethod = (TextView) convertView.findViewById(R.id.payment_method);
        paymentMethod.setText(orderdatumArrayList.get(position).getMethod());

        return convertView;
    }
}
