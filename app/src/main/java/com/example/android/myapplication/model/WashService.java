package com.example.android.myapplication.model;

import android.os.AsyncTask;

import com.example.android.myapplication.R;
import com.example.android.myapplication.activity.WashInitActivity;
import com.example.android.myapplication.utils.HttpManager;
import com.example.android.myapplication.utils.RequestPackage;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by abedch on 3/31/2016.
 */
public class WashService {

    private WashInitActivity washInitActivity;

    public WashService(WashInitActivity washInitActivity) {
        this.washInitActivity = washInitActivity;
    }


    public void isWashOrderWashed(String orderNum,Integer idAppUser) {
        Map<String, String> params = new HashMap<>();
        params.put("orderNum", orderNum);
        params.put("idAppUser", idAppUser+"");
        RequestPackage p = new RequestPackage();
        p.setUri(washInitActivity.getResources().getString(idAppUser!=1?R.string.isWashOrderWashedProd:R.string.isWashOrderWashedDev));
        p.setParams(params);
        IsOrderWashedTask myTask = new IsOrderWashedTask();
        myTask.execute(p);
    }


    private class IsOrderWashedTask extends AsyncTask<RequestPackage, String, String> {


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
            Boolean response = null;
            if(result!=null&&result.length()>0)
            {
               result = result.replace("\n","");
                response = Boolean.valueOf(result);
            }
           washInitActivity.initWashOrder(response);
        }
    }





}
