package com.example.android.myapplication.utils;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by abedch on 9/10/2015.
 */
public class HttpManager {


    public static String getData(RequestPackage p) {

        BufferedReader reader = null;
        String uri = p.getUri();
        if (p.getMethod().equals("GET")) {
            uri += "?" + p.getEncodedParams();
        }

        try {
            URL url = new URL(uri);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(5000);

            StringBuilder sb = new StringBuilder();

            con.getResponseCode();
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));


            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }


    }



}
