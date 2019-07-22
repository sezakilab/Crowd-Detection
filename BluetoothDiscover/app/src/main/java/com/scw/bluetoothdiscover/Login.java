package com.scw.bluetoothdiscover;


import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class Login extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {
    private final AppCompatActivity activity = Login.this;

    //private InputValidation inputValidation;

    private static Handler handler_data;
    private static final int SUCCESS_MSG = 1;
    //获取失败返回message
    private static final int FAILURE_MSG = 0;

    private Button loginButton;
    private Button createButton;
    private Button enterButton;
    private Button checkButton; // test

    private EditText nameInput;
    private EditText passwordInput;
    private TextView nameCheck;
    private TextView passwordCheck;

    private InputMethodManager imm;

    private HttpRequest httpRequest;
    private Scanner_BTLE scanner_btle; //test
    Handler handler;//test
    Runnable runnable;//test
    JSONArray deviceArray = new JSONArray();//test
    Location userLocation = new Location("");//test

    private boolean nameCheckResult = false;
    private boolean passwordCheckResult = false;

    private User user;


    String checkResult = null;
    String username = null;
    String password = null;
    String loginResult = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        initViews();
        initObjects();

        handler_data = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1://SUCCESS_MSG
                        switch (checkResult) {
                            case "nothing":
                                System.out.println("Test fail to connect");
                                break;
                            case "null":
                                System.out.println("Test id or password");
                                break;
                            case "good":
                                System.out.println("good");
                                Intent intent = new Intent(Login.this, MapBoxActivity.class);
                                intent.putExtra("username", username);
                                intent.putExtra("login", true);
                                startActivity(intent);
                                break;

                        }
                        break;
                    case 0://FAILURE_MSG
                        System.out.println("Test fail message");
                        //Utils.toast(getApplicationContext(), "fail to connect the server");
                        break;
                }
            }
        };


    }


    /**
     * This method is to initialize views
     */
    private void initViews() {

        loginButton = (Button) findViewById(R.id.login);
        createButton = (Button) findViewById(R.id.create);
        enterButton = (Button) findViewById(R.id.enter);
        checkButton = (Button) findViewById(R.id.check);

        nameInput = (EditText) findViewById(R.id.nameInput);
        passwordInput = (EditText) findViewById(R.id.passwordInput);

        nameCheck = (TextView) findViewById(R.id.nameCheck);
        passwordCheck = (TextView) findViewById(R.id.passwordCheck);

        loginButton.setOnClickListener(this);
        createButton.setOnClickListener(this);
        enterButton.setOnClickListener(this);
        checkButton.setOnClickListener(this);

        nameInput.setOnFocusChangeListener(this);
        passwordInput.setOnFocusChangeListener(this);

        loginButton.setEnabled(false);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

    }

    /**
     * This method is to initialize objects to be used
     */
    private void initObjects() {
        //HttpRequest
        //httpRequest = new HttpRequest(getString(R.string.server_test));
        httpRequest = new HttpRequest(this);
        user = new User();
        scanner_btle = new Scanner_BTLE(this, -100);
        userLocation.setLatitude(35.664065);
        userLocation.setLongitude(139.677224);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                deviceJSON(scanner_btle.result());
                scanner_btle.clean();
                Thread testThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        readJSON(httpRequest.doPostData(deviceArray, (float) 35.664065, (float) 139.677224, "testname"));

                    }

                });
                testThread.start();
                try {
                    testThread.join();
                } catch (
                        InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (Login.this.getCurrentFocus() != null) {
                if (Login.this.getCurrentFocus().getWindowToken() != null) {
                    imm.hideSoftInputFromWindow(Login.this.getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    checkInput();
                }
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * This implemented method is to listen the click on view
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                login();
                break;
            case R.id.create:
                // Navigate to Register Activity
                Intent intentRegister = new Intent(getApplicationContext(), Register.class);
                startActivity(intentRegister);
                //finish();
                break;
            case R.id.enter:
                //scanner_btle.start();
                //handler.postDelayed(runnable, 5000);


                // Navigate to Map Activity
                Intent intentEnter = new Intent(getApplicationContext(), MapBoxActivity.class);
                //intentEnter.putExtra("login", false);
                startActivity(intentEnter);

                break;
            case R.id.check:
                //Intent intentCheck = new Intent(getApplicationContext(), Check.class);
                //startActivity(intentCheck);
                scanner_btle.start();
                handler.postDelayed(runnable, 5000);
                break;
            default:
                break;
        }
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.nameInput:
                if (hasFocus) {

                } else {
                    checkInput();
                }
                break;
            case R.id.passwordInput:
                if (hasFocus) {

                } else {
                    checkInput();
                }
                break;
        }

    }

    //test
    private void readJSON(String showHttp) {
        try {
            JSONArray jsonArray = new JSONArray(showHttp);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                //int user_to_device_id = jsonObject.getInt("user_to_device_id");
                //String date = jsonObject.getString("date");
                double lat_locate = jsonObject.getDouble("lat");
                double long_locate = jsonObject.getDouble("long");
                String mac_address = jsonObject.getString("mac_address");
                int rssi = jsonObject.getInt("rssi");
                System.out.println("Test " + lat_locate + " " + long_locate + " " + mac_address + rssi);
                // Set Marker
//                mapboxMap.addMarker(new MarkerOptions()
//                                .position(new LatLng(lat_locate, long_locate))
//                        //.title("rssi " + rssi)
//                        //.snippet(mac_address)
//                );
                //System.out.println(user_to_device_id + date + lat_locate + long_locate + mac_address + rssi);
                //idList.add(id);
                //nameList.add(name);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //test
    private void deviceJSON(HashMap<String, Integer> resultList) {
        //  delete last json
        try {
            deviceArray = new JSONArray("[]");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Iterator iter = resultList.entrySet().iterator();
        //HashMap<String, Float> resultList = new HashMap<>();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            int value = (int) entry.getValue();
            JSONObject device = new JSONObject();
            try {
                device.put("address", key);
                device.put("rssi", value);
                deviceArray.put(device);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private void login() {
        user.setName(nameInput.getText().toString().trim());
        user.setPassword(passwordInput.getText().toString());
        Thread loginThread = new Thread(new Runnable() {
            @Override
            public void run() {
                loginResult = httpRequest.doPostLogin(user.getName(), user.getPassword());
            }
        });
        loginThread.start();

    }

    private void checkInput() {
        if (nameInput.getText().toString().trim().equals("")) {
            nameCheckResult = false;
            nameCheck.setText("Input Name");
        } else {
            nameCheckResult = true;
            nameCheck.setText("");
        }
        if (passwordInput.getText().toString().equals("")) {
            passwordCheckResult = false;
            passwordCheck.setText("Input Name");
        } else {
            passwordCheckResult = true;
            passwordCheck.setText("");
        }
        if (nameCheckResult && passwordCheckResult) {
            loginButton.setEnabled(true);
        } else {
            loginButton.setEnabled(false);
        }
    }

    /**
     * This method is to validate the input text fields and verify login credentials from SQLite
     */
    private void verifyInfo() {

        // trim >> remove space
        username = nameInput.getText().toString().trim();
        password = passwordInput.getText().toString();
        new Thread(new Runnable() {
            @Override
            public void run() {
                checkResult = httpRequest.doPostLogin(username, password);
                if (checkResult.isEmpty()) {
                    handler_data.obtainMessage(FAILURE_MSG, checkResult).sendToTarget();

                } else {
                    handler_data.obtainMessage(SUCCESS_MSG, checkResult).sendToTarget();
                }
            }
        }).start();

    }

    /**
     * This method is to empty all input edit text
     */
    private void emptyInputEditText() {
        nameInput.setText(null);
        nameCheck.setText(null);
    }


}
