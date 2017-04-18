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

public class DeliveredOrdersListViewAdapter extends BaseAdapter {

    private static final int MY_REQUEST_CODE = 967;
    private Context context;
    private ArrayList<Orderdatum> orderdatumArrayList;

    public DeliveredOrdersListViewAdapter(Context context, ArrayList<Orderdatum> orderdatumArrayList) {
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
            convertView = inflater.inflate(R.layout.delivered_orders_list_item, parent, false);

        }

        TextView incrementId = (TextView) convertView.findViewById(R.id.increment_id);
        incrementId.setText(orderdatumArrayList.get(position).getIncrementId());

        TextView orderShipId = (TextView) convertView.findViewById(R.id.order_ship_id);
        orderShipId.setText(orderdatumArrayList.get(position).getOrdershipid());

        TextView payMethod = (TextView) convertView.findViewById(R.id.pay_method);
        payMethod.setText(orderdatumArrayList.get(position).getMethod());

        TextView actionDate = (TextView) convertView.findViewById(R.id.action_date);
        actionDate.setText(orderdatumArrayList.get(position).getActionDate());

        return convertView;
    }
}
