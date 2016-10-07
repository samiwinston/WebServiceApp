package com.example.android.myapplication.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.myapplication.R;
import com.example.android.myapplication.model.UserBean;
import com.example.android.myapplication.utils.HttpManager;
import com.example.android.myapplication.utils.JSonParser;
import com.example.android.myapplication.utils.RequestPackage;

import org.acra.ACRA;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends Activity {

    ProgressBar pb;

    private Boolean mockValidation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_view);
        pb = (ProgressBar) findViewById(R.id.loginProgressBar);
        pb.setVisibility(View.INVISIBLE);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login_menu, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void loginResult(String result){


        if(result == null)
        {
            Toast.makeText(this, "Can not connect to server", Toast.LENGTH_SHORT).show();
            return;
        }

        if(result.length()==0) // it will return an empty string
        {
            Toast.makeText(this, "Wrong credentials", Toast.LENGTH_SHORT).show();
            return;
        }


        try{
            UserBean userBean = JSonParser.parseUserBeanFeed(result);
            initApp(userBean);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            // save error
            Toast.makeText(this, "Illegal Error "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    public void loginHandler(View view) {

        TextView userText = (TextView) findViewById(R.id.usernameInput);
        TextView passText = (TextView) findViewById(R.id.passwordInput);

        if ((userText.getText() == null || userText.getText().length() == 0) ||
                ((passText.getText() == null || passText.getText().length() == 0))) {
            Toast.makeText(this, "Enter your credentials", Toast.LENGTH_SHORT).show();
            return;
        }

        if(mockValidation)
        {
            if (userText.getText().toString().equals("ach") && passText.getText().toString().equals("test")) {
                initApp(null);

            } else {
                Toast.makeText(this, "Wrong credentials", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Map<String, String> params = new HashMap<>();
            params.put("username", userText.getText().toString());
            params.put("password", passText.getText().toString());
            String url = getResources().getString(R.string.checkUserProd);

            requestData(url, params);
        }



    }

    private void requestData(String uri, Map<String, String> params) {




        RequestPackage p = new RequestPackage();
        p.setUri(uri);
        p.setParams(params);

        MyTask task = new MyTask();
        task.execute(p);
    }

    private void initApp(UserBean userBean) {
        Intent toWashCode = new Intent(this, WashInitActivity.class);
        toWashCode.putExtra("idAppUser",userBean.getIdAppUser());
        startActivity(toWashCode);
        finish();
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
            loginResult(result);
            pb.setVisibility(View.INVISIBLE);
        }


    }

}
