package com.yoscholar.deliveryboy.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.yoscholar.deliveryboy.R;
import com.yoscholar.deliveryboy.pojo.OrderAccepted;
import com.yoscholar.deliveryboy.retrofitPojo.acceptAnOrder.AcceptOrder;
import com.yoscholar.deliveryboy.retrofitPojo.ordersToAccept.AcceptOrders;
import com.yoscholar.deliveryboy.utils.AppPreference;
import com.yoscholar.deliveryboy.utils.RetrofitApi;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by agrim on 11/2/17.
 */

public class AcceptOrdersListViewAdapter extends BaseAdapter {

    private Context context;
    private AcceptOrders acceptOrders;
    private ProgressDialog progressDialog;

    public AcceptOrdersListViewAdapter(Context context, AcceptOrders acceptOrders) {
        this.context = context;
        this.acceptOrders = acceptOrders;
        this.progressDialog = new ProgressDialog(this.context);
        this.progressDialog.setIndeterminate(true);
        this.progressDialog.setMessage("Please wait....");
        this.progressDialog.setCancelable(false);
        this.progressDialog.setCanceledOnTouchOutside(false);
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

        TextView acceptedTextView = (TextView) convertView.findViewById(R.id.accepted_text_view);

        if (acceptOrders.getOrderdata().get(position).getAcceptStatus() == 0) {

            acceptButton.setVisibility(View.VISIBLE);
            acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText(context, "Position : " + position, Toast.LENGTH_SHORT).show();
                    acceptAnOrder(acceptOrders.getOrderdata().get(position).getOrdershipid());
                }
            });

            acceptedTextView.setVisibility(View.GONE);

        } else {

            acceptButton.setVisibility(View.GONE);
            acceptedTextView.setVisibility(View.VISIBLE);

        }

        return convertView;
    }

    private void acceptAnOrder(String orderShipId) {

        progressDialog.show();

        RetrofitApi.ApiInterface apiInterface = RetrofitApi.getApiInterfaceInstance();

        Call<AcceptOrder> acceptOrderCall = apiInterface.acceptAnOrder(
                orderShipId,//orderShipId
                AppPreference.getString(context, AppPreference.TOKEN)//jwt token
        );

        acceptOrderCall.enqueue(new Callback<AcceptOrder>() {

            @Override
            public void onResponse(Call<AcceptOrder> call, Response<AcceptOrder> response) {

                progressDialog.dismiss();

                if (response.isSuccessful()) {

                    if (response.body().getStatus().equalsIgnoreCase("success")) {

                        Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                        EventBus.getDefault().post(new OrderAccepted(true));

                    } else if (response.body().getStatus().equalsIgnoreCase("failure")) {

                        //show message
                        Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                        EventBus.getDefault().post(new OrderAccepted(false));

                    }

                } else {

                    Toast.makeText(context, "Some Error", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onFailure(Call<AcceptOrder> call, Throwable t) {

                progressDialog.dismiss();
                Toast.makeText(context, "Network problem", Toast.LENGTH_SHORT).show();
                //Log.d("VICKY", call.request().url().toString());

            }
        });
    }


}
