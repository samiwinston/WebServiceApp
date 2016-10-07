package com.example.android.myapplication.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by abedch on 9/9/2015.
 */
public class QueueWashResponse {

    public final static int SUCCESS = 1;
    public final static int WASH_NOT_FOUND= 2;
    public final static int WASH_SERV_NOT_FOUND= 3;
    public final static int EXCEPTION= 4;

    private int washTypeCode;
    private int addonsCode;
    private int responseCode;


    public QueueWashResponse() {
    }

    public QueueWashResponse(int washTypeCode, int addonsCode) {
        this.washTypeCode = washTypeCode;
        this.addonsCode = addonsCode;
    }


    public String getErrorType(int errorCode){

        switch (errorCode){
            case WASH_NOT_FOUND:
                return "Wash Order Not Found";
            case WASH_SERV_NOT_FOUND:
                return "Wash Service Not Found";
            case EXCEPTION:
                return "Illegal Error, please contact the admin";
        }

        return "Illegal Error Code "+errorCode+", please contact the admin";
    }

    public void setFromJson(JSONObject jsonObject) throws JSONException {
        this.washTypeCode = jsonObject.getInt("washTypeCode");
        this.addonsCode = jsonObject.getInt("addonsCode");
        this.responseCode= jsonObject.getInt("responseCode");
    }



    public int getAddonsCode() {
        return addonsCode;
    }

    public void setAddonsCode(int addonsCode) {
        this.addonsCode = addonsCode;
    }

    public int getWashTypeCode() {
        return washTypeCode;
    }

    public void setWashTypeCode(int washTypeCode) {
        this.washTypeCode = washTypeCode;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }
}
