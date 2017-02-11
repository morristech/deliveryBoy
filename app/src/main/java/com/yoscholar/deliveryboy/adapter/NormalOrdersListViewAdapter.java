package com.yoscholar.deliveryboy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yoscholar.deliveryboy.R;
import com.yoscholar.deliveryboy.retrofitPojo.normalOrders.NormalOrders;

/**
 * Created by agrim on 11/2/17.
 */

public class NormalOrdersListViewAdapter extends BaseAdapter {

    private Context context;
    private NormalOrders normalOrders;

    public NormalOrdersListViewAdapter(Context context, NormalOrders normalOrders) {
        this.context = context;
        this.normalOrders = normalOrders;
    }

    @Override
    public int getCount() {
        return normalOrders.getOrderdata().size();
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
            convertView = inflater.inflate(R.layout.normal_orders_list_item, parent, false);

        }

        TextView incrementId = (TextView) convertView.findViewById(R.id.increment_id);
        incrementId.setText("#" + normalOrders.getOrderdata().get(position).getIncrementId());

        TextView address = (TextView) convertView.findViewById(R.id.address);
        address.setText(normalOrders.getOrderdata().get(position).getAddress() + ",\n" +
                normalOrders.getOrderdata().get(position).getCity() + ", " +
                normalOrders.getOrderdata().get(position).getPincode());

        TextView total = (TextView) convertView.findViewById(R.id.total);
        if (normalOrders.getOrderdata().get(position).getTotal().equalsIgnoreCase("Prepaid"))
            total.setText(normalOrders.getOrderdata().get(position).getTotal());
        else
            total.setText("Rs " + normalOrders.getOrderdata().get(position).getTotal());

        return convertView;
    }
}
