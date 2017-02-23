package com.yoscholar.deliveryboy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.yoscholar.deliveryboy.R;
import com.yoscholar.deliveryboy.retrofitPojo.ordersToAccept.AcceptOrders;

/**
 * Created by agrim on 11/2/17.
 */

public class AcceptOrdersListViewAdapter extends BaseAdapter {

    private Context context;
    private AcceptOrders acceptOrders;

    public AcceptOrdersListViewAdapter(Context context, AcceptOrders acceptOrders) {
        this.context = context;
        this.acceptOrders = acceptOrders;
    }

    @Override
    public int getCount() {
        return acceptOrders.getOrderdata().size();
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
            convertView = inflater.inflate(R.layout.accept_orders_list_item, parent, false);

        }

        TextView incrementId = (TextView) convertView.findViewById(R.id.increment_id);
        incrementId.setText("#" + acceptOrders.getOrderdata().get(position).getIncrementId());

        Button acceptButton = (Button) convertView.findViewById(R.id.accept_button);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(context, "Position : " + position, Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }
}
