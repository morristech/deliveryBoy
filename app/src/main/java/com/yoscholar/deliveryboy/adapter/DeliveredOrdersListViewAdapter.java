package com.yoscholar.deliveryboy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yoscholar.deliveryboy.R;
import com.yoscholar.deliveryboy.couchDB.CouchBaseHelper;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by agrim on 27/2/17.
 */

public class DeliveredOrdersListViewAdapter extends BaseAdapter {

    private static final int MY_REQUEST_CODE = 967;
    private Context context;
    private ArrayList<Map<String, Object>> orderMapArrayList;

    public DeliveredOrdersListViewAdapter(Context context, ArrayList<Map<String, Object>> orderMapArrayList) {
        this.context = context;
        this.orderMapArrayList = orderMapArrayList;
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
            convertView = inflater.inflate(R.layout.delivered_orders_list_item, parent, false);

        }

        TextView incrementId = (TextView) convertView.findViewById(R.id.increment_id);
        incrementId.setText(orderMapArrayList.get(position).get(CouchBaseHelper.INCREMENT_ID).toString());

        TextView orderShipId = (TextView) convertView.findViewById(R.id.order_ship_id);
        orderShipId.setText(orderMapArrayList.get(position).get(CouchBaseHelper.ORDER_SHIP_ID).toString());

        TextView payMode = (TextView) convertView.findViewById(R.id.pay_mode);
        if (orderMapArrayList.get(position).get(CouchBaseHelper.METHOD).toString().equals(CouchBaseHelper.PAYMENT_COD))

            payMode.setText(orderMapArrayList.get(position).get(CouchBaseHelper.PAY_MODE).toString() + " : " + orderMapArrayList.get(position).get(CouchBaseHelper.TOTAL).toString());
        else

            payMode.setText(orderMapArrayList.get(position).get(CouchBaseHelper.PAY_MODE).toString());

        TextView actionDate = (TextView) convertView.findViewById(R.id.action_date);
        actionDate.setText(orderMapArrayList.get(position).get(CouchBaseHelper.ACTION_DATE).toString());

        return convertView;
    }
}
