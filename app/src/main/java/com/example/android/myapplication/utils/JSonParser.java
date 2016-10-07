package com.example.android.myapplication.utils;

import com.example.android.myapplication.model.QueueWashResponse;
import com.example.android.myapplication.model.UserBean;

import org.json.JSONObject;

/**
 * Created by abedch on 9/11/2015.
 */
public class JSonParser {


    public static QueueWashResponse parseRequestedWashFeed(String content) throws Exception {


            JSONObject jsonObject = new JSONObject(content);
            QueueWashResponse bean = new QueueWashResponse();
            bean.setFromJson(jsonObject);
            return bean;



    }

    public static UserBean parseUserBeanFeed(String content) throws Exception {


        JSONObject jsonObject = new JSONObject(content);
        UserBean bean = new UserBean();
        bean.setFromJson(jsonObject);
        return bean;



    }

}
