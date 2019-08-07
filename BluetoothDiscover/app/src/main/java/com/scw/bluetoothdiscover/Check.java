package com.scw.bluetoothdiscover;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.ScrollView;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.IOException;


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
        //httpRequest = new HttpRequest(this); //test
        try {
            String data = readFile("test");
            checkView.setText(data);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

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
