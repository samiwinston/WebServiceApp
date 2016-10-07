package com.example.android.myapplication.model;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.android.myapplication.R;
import com.example.android.myapplication.utils.HttpManager;
import com.example.android.myapplication.utils.RequestPackage;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

/**
 * Created by abedch on 3/30/2016.
 */
public class WashTimerTask extends TimerTask {

    private String orderNumber;
    private Integer idAppUser;
    private Context context;

    public WashTimerTask(String orderNumber,Integer idAppUser,Context context){
        this.orderNumber = orderNumber;
        this.idAppUser = idAppUser;
        this.context = context;
    }

    @Override
    public void run() {
        Log.d("TIMER_Task","Will call url for order number "+orderNumber);
        Map<String, String> params = new HashMap<>();
        params.put("orderNum", orderNumber);
        params.put("idAppUser", idAppUser+"");
        String uri = context.getResources().getString(idAppUser !=1 ?R.string.setWashOrderToWashedProd:R.string.setWashOrderToWashedDev);
        setWashOrderToWashed(uri, params);
    }


    private void setWashOrderToWashed(String uri, Map<String, String> params) {
        RequestPackage p = new RequestPackage();
        p.setUri(uri);
        p.setParams(params);
        SetWashOrderToWashedTask setWashOrderToWashedTask = new SetWashOrderToWashedTask();
        setWashOrderToWashedTask.execute(p);
    }





    private class SetWashOrderToWashedTask extends AsyncTask<RequestPackage, String, String> {

        // has access to main thread

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(RequestPackage... params) {
            String content = HttpManager.getData(params[0]);
            return content;
        }

        @Override
        protected void onPostExecute(String result) {
            setWashOrderToWashedCBH(result);
        }
    }



    private void setWashOrderToWashedCBH(String result) {

        if (result != null) {
            Toast.makeText(context, "Wash order " + orderNumber + " has been completed", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Wash order status could not be updated, please contact the admin", Toast.LENGTH_LONG).show();
        }

    }

}
