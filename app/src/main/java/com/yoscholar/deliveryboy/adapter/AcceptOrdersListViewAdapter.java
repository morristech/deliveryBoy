package com.yoscholar.deliveryboy.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.Database;
import com.yoscholar.deliveryboy.R;
import com.yoscholar.deliveryboy.couchDB.CouchBaseHelper;
import com.yoscholar.deliveryboy.pojo.OrderAccepted;
import com.yoscholar.deliveryboy.retrofitPojo.acceptAnOrder.AcceptOrder;
import com.yoscholar.deliveryboy.retrofitPojo.ordersToAccept.AcceptOrders;
import com.yoscholar.deliveryboy.retrofitPojo.ordersToAccept.Orderdatum;
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

        LinearLayout idContainer  = (LinearLayout) convertView.findViewById(R.id.id_container);

        TextView incrementId = (TextView) convertView.findViewById(R.id.increment_id);
        incrementId.setText("ORDER ID : " + acceptOrders.getOrderdata().get(position).getIncrementId());

        TextView orderShipId = (TextView) convertView.findViewById(R.id.order_ship_id);
        orderShipId.setText("SHIP ID : "+acceptOrders.getOrderdata().get(position).getOrdershipid());


        Button acceptButton = (Button) convertView.findViewById(R.id.accept_button);

        Button declineButton = (Button) convertView.findViewById(R.id.decline_button);

        if (acceptOrders.getOrderdata().get(position).getAcceptStatus().equals("0")) {

            idContainer.setBackgroundResource(android.R.color.white);

            acceptButton.setVisibility(View.VISIBLE);
            declineButton.setVisibility(View.GONE);

            acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Toast.makeText(context, "Position : " + position, Toast.LENGTH_SHORT).show();
                    acceptOrDeclineAnOrder(acceptOrders.getOrderdata().get(position).getOrdershipid(), acceptOrders.getOrderdata().get(position), "1");
                }
            });

        } else if (acceptOrders.getOrderdata().get(position).getAcceptStatus().equals("1")) {

            idContainer.setBackgroundResource(R.color.colorPrimary);

            acceptButton.setVisibility(View.GONE);
            declineButton.setVisibility(View.VISIBLE);

            declineButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!checkIfTheOrderIsPresentInFailedOrders(acceptOrders.getOrderdata().get(position).getOrdershipid())) {

                        //Toast.makeText(context, "Position : " + position, Toast.LENGTH_SHORT).show();
                        acceptOrDeclineAnOrder(acceptOrders.getOrderdata().get(position).getOrdershipid(), acceptOrders.getOrderdata().get(position), "0");
                    } else {

                        Toast.makeText(context, "This order is present in failed orders. Go to failed orders to deliver it.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

        return convertView;
    }

    private boolean checkIfTheOrderIsPresentInFailedOrders(String orderShipId) {

        Database database = CouchBaseHelper.openCouchBaseDB(context);

        return CouchBaseHelper.checkIfAnOrderIsPresentInFailedOrders(database, orderShipId);
    }

    private void acceptOrDeclineAnOrder(String orderShipId, final Orderdatum orderdatum, final String flag) {

        progressDialog.show();

        RetrofitApi.ApiInterface apiInterface = RetrofitApi.getApiInterfaceInstance();

        Call<AcceptOrder> acceptOrderCall = apiInterface.acceptOrDeclineAnOrder(
                orderShipId,//orderShipId
                AppPreference.getString(context, AppPreference.TOKEN),//jwt token
                flag,
                AppPreference.getString(context, AppPreference.NAME),//db name
                orderdatum.getIncrementId()
        );

        acceptOrderCall.enqueue(new Callback<AcceptOrder>() {

            @Override
            public void onResponse(Call<AcceptOrder> call, Response<AcceptOrder> response) {

                progressDialog.dismiss();

                if (response.isSuccessful()) {

                    if (response.body().getStatus().equalsIgnoreCase("success")) {

                        //Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                        if (flag.equals("1"))//save the accepted order in DB
                            saveTheAcceptedOrderInDb(orderdatum);
                        else if (flag.equals("0"))////delete the declined order from DB
                            deleteTheAcceptedOrderFromDB(orderdatum);

                        //reload the list of orders to accept
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

    private void deleteTheAcceptedOrderFromDB(Orderdatum orderdatum) {

        Database database = CouchBaseHelper.openCouchBaseDB(context);

        if (CouchBaseHelper.deleteAnAcceptedOrderFromDB(database, orderdatum.getOrdershipid()))
            Toast.makeText(context, "Order declined successfully.", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(context, "Order could not be declined.", Toast.LENGTH_SHORT).show();

    }

    private void saveTheAcceptedOrderInDb(Orderdatum orderdatum) {

        Database database = CouchBaseHelper.openCouchBaseDB(context);

        if (CouchBaseHelper.saveAnAcceptedOrderInDB(database, orderdatum))
            Toast.makeText(context, "Order accepted successfully.", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(context, "Order could not be accepted.", Toast.LENGTH_SHORT).show();

    }


}
