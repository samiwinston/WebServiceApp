package com.example.android.myapplication.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.myapplication.model.QueueWashResponse;
import com.example.android.myapplication.model.WashService;
import com.example.android.myapplication.model.WashTimerTask;
import com.example.android.myapplication.utils.HttpManager;
import com.example.android.myapplication.R;
import com.example.android.myapplication.utils.JSonParser;
import com.example.android.myapplication.utils.PlcCommHandler;
import com.example.android.myapplication.utils.RequestPackage;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class WashInitActivity extends Activity {

    ProgressBar pb;
    String orderNum;
    PlcCommHandler plcCommHandler;
    Integer idAppUser;
    String errorPhaseType = "Is Ready To Wash";
    EditText editText;
    Button startWashBtn;
    Timer timer;
    TimerTask timerTask;
    private long timerDelay;
    WashService washService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wash_init_view);
        pb = (ProgressBar) findViewById(R.id.progressBar);
        pb.setVisibility(View.INVISIBLE);
        Intent mIntent = getIntent();
        idAppUser = mIntent.getIntExtra("idAppUser", 1);
        editText = (EditText) findViewById(R.id.washCodeInput);
        startWashBtn = (Button) findViewById(R.id.startWashBtn);
        washService = new WashService(this);

        initPlcCommHandler();
        initTimerDelay();
    }

    private void initTimerDelay() {
        if (idAppUser == 1)
            timerDelay = 10000; //10 sec delay
        else {
            timerDelay = 210000; // 3.5  min delay
        }
    }

    private void postLog(String msg) {
        if (idAppUser != 1) {
            Map<String, String> params = new HashMap<>();
            params.put("idAppUser", idAppUser + "");
            params.put("description", msg);
            String uri = getResources().getString(R.string.postLogDataProd);
            postLogData(uri, params);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.wash_menu, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    private void initPlcCommHandler() {
        try {
            String plcAddress = getResources().getString(R.string.plcAddressProd);
            Integer plcRegRef = getResources().getInteger(R.integer.plcHRegRef1);
            Integer startCoilRef = getResources().getInteger(R.integer.startCoilRef);
            Integer lockoutCoilRef = getResources().getInteger(R.integer.lockoutCoilRef);

            plcCommHandler = new PlcCommHandler(plcAddress, plcRegRef, startCoilRef, lockoutCoilRef);
        } catch (Exception e) {
            // save initPlcCommHandlerError
            postLog("On initPlcCommHandler failure exception is " + e.getMessage());
            e.printStackTrace();
        }

    }


    public void submitWashHandler(View view) {

        String enteredText = editText.getText().toString();

        if (enteredText == null || enteredText.length() == 0 || enteredText.startsWith("0")) {
            Toast.makeText(this, "Invalid Entry", Toast.LENGTH_LONG).show();
            return;
        }


        if (isOnline()) {
            orderNum = editText.getText().toString();
            washService.isWashOrderWashed(orderNum, idAppUser);
        } else {
            Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
        }


    }

    public void initWashOrder(Boolean isWashOrderWashed) {


        if(isWashOrderWashed==null)
        {
            Toast.makeText(this, "Can not find order "+orderNum, Toast.LENGTH_LONG).show();
            return;
        }

        if(isWashOrderWashed)
        {
            Toast.makeText(this, "Order "+orderNum+" was already washed", Toast.LENGTH_LONG).show();
            editText.setText("");
        }
        else
        {
            Map<String, String> params = new HashMap<>();
            params.put("orderNum", orderNum);
            String uri = getResources().getString(idAppUser !=1 ?R.string.getWashCodesProd:R.string.getWashCodesDev);
            requestData(uri, params);
        }

    }


    public void handleWash(String result) {
        String initWashResp = "";
        if (result == null) {
            postLog("On handle wash for order " + orderNum + " result is null");

            Toast.makeText(this, "Illegal Error", Toast.LENGTH_SHORT).show();
            return;
        }
        QueueWashResponse queueWashResponse = null;
        try {

            queueWashResponse = JSonParser.parseRequestedWashFeed(result);
            //queueWashResponse.setWashTypeCode(1);
            if (queueWashResponse.getResponseCode() != QueueWashResponse.SUCCESS) {
                String msg = "Wash Failure for order num " + orderNum + ": " + queueWashResponse.getErrorType(queueWashResponse.getResponseCode());
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                postLog(msg);
                return;
            }

            if(idAppUser!=1){
                if (plcCommHandler.isReadyForWash()) {
                    errorPhaseType = "Is Init Wash";
                    initWashResp = plcCommHandler.initWash(queueWashResponse, Integer.parseInt(orderNum));
                } else {
                    postLog("On " + errorPhaseType + " for order num " + orderNum + " Conveyor is busy");
                    Toast.makeText(this, "Conveyor is busy for order num " + orderNum, Toast.LENGTH_LONG).show();
                    return;
                }
            }



        } catch (Exception e) {
            Toast.makeText(this, "Illegal modbus error", Toast.LENGTH_LONG).show();
            postLog("On " + errorPhaseType + " for order num " + orderNum + " on plc address " + plcCommHandler.getPlcAddress() + initWashResp + " Exception is " + e.getMessage());
            return;
        }


        Toast.makeText(this, "Wash has been initialized for order num " + orderNum + " and wash package " + queueWashResponse.getWashTypeCode(), Toast.LENGTH_LONG).show();
        postLog("Wash has been initialized for order num " + orderNum + " and wash package " + queueWashResponse.getWashTypeCode());

        startTimer();
        editText.setText("");
    }


    public void startTimer() {
        // set a new timer
        timer = new Timer();
        timerTask = new WashTimerTask(orderNum, idAppUser, this);
        timer.schedule(timerTask, timerDelay);
    }


    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }


    }


    private void requestData(String uri, Map<String, String> params) {
        RequestPackage p = new RequestPackage();
        p.setUri(uri);
        p.setParams(params);
        MyTask myTask = new MyTask();
        myTask.execute(p);
    }

    private void postLogData(String uri, Map<String, String> params) {
        RequestPackage p = new RequestPackage();
        p.setUri(uri);
        p.setParams(params);
        MyLogTask myLogTask = new MyLogTask();
        myLogTask.execute(p);
    }


    private class MyTask extends AsyncTask<RequestPackage, String, String> {

        // has access to main thread
        @Override
        protected void onPreExecute() {
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(RequestPackage... params) {
            String content = HttpManager.getData(params[0]);
            return content;
        }

        @Override
        protected void onPostExecute(String result) {
            pb.setVisibility(View.INVISIBLE);
            handleWash(result);
        }
    }

    private class MyLogTask extends AsyncTask<RequestPackage, String, String> {


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
        }
    }


}
