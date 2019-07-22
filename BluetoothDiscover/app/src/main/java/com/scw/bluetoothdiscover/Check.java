package com.scw.bluetoothdiscover;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;


import okhttp3.RequestBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Check extends AppCompatActivity {

    private ScrollView scrollView;
    private TextView checkView;
    HttpRequest httpRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);
        scrollView = (ScrollView) findViewById(R.id.scroll);
        checkView = (TextView) findViewById(R.id.check);
        httpRequest = new HttpRequest(this); //test
        try {
            String data = readFile("test");

            /*
            new Thread(new Runnable() {
                @Override
                public void run() {
                    httpRequest.phonedata(data);
                }
            }).start(); */

            checkView.setText(data);
            //saveToSDCard("test", data);
            //System.out.println(data);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //scrollView.addView(checkView);
    }

    public String readFile(String fileName) throws IOException {
        String res = "";
        try {
            FileInputStream fin = openFileInput(fileName);
            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read(buffer);
            res = new String(buffer, "UTF-8");
            fin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }


}
